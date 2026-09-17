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

/**
 * 项目数据访问层。
 *
 * <p>所有对外查询都会排除软删除记录（{@code deleted = true}）。
 */
public interface ProjectRepository extends JpaRepository<Project, Long> {

    /** 按主键查询未删除的项目。 */
    Optional<Project> findByIdAndDeletedFalse(Long id);

    /** 判断未删除的项目中是否已存在该编号。 */
    boolean existsByCodeAndDeletedFalse(String code);

    /** 判断除指定主键外，是否已存在该编号的未删除项目（更新时排除自身）。 */
    boolean existsByCodeAndDeletedFalseAndIdNot(String code, Long id);

    /**
     * 按关键字与状态分页查询，按更新时间倒序由调用方通过 {@link Pageable} 指定。
     *
     * @param keyword 为 {@code null} 时不参与过滤；否则忽略大小写模糊匹配项目编号或名称
     * @param status  为 {@code null} 时不参与过滤
     * @param pageable 分页与排序参数
     */
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

    /**
     * 统计与 {@link #search} 相同筛选条件下的金额合计。
     *
     * @param keyword 同 {@link #search}
     * @param status  同 {@link #search}
     * @return 应收/应付/实收/实付合计，无数据时为 0
     */
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
