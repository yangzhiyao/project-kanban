package com.kanban.project.repository;

import com.kanban.project.dto.ProjectSummary;
import com.kanban.project.model.Project;
import com.kanban.project.model.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByIdAndDeletedFalse(Long id);

    boolean existsByCodeAndDeletedFalse(String code);

    boolean existsByCodeAndDeletedFalseAndIdNot(String code, Long id);

    @Query("""
            select p from Project p
            where p.deleted = false
              and (:status is null or p.status = :status)
              and (:keyword is null
                   or lower(p.code) like lower(concat('%', :keyword, '%'))
                   or lower(p.name) like lower(concat('%', :keyword, '%')))
            """)
    Page<Project> search(@Param("keyword") String keyword,
                         @Param("status") ProjectStatus status,
                         Pageable pageable);

    @Query("""
            select new com.kanban.project.dto.ProjectSummary(
                sum(p.receivableAmount),
                sum(p.payableAmount),
                sum(p.receivedAmount),
                sum(p.paidAmount))
            from Project p
            where p.deleted = false
              and (:status is null or p.status = :status)
              and (:keyword is null
                   or lower(p.code) like lower(concat('%', :keyword, '%'))
                   or lower(p.name) like lower(concat('%', :keyword, '%')))
            """)
    ProjectSummary summarize(@Param("keyword") String keyword,
                             @Param("status") ProjectStatus status);
}
