package com.kanban.project.model;

/**
 * 项目状态。
 */
public enum ProjectStatus {

    /** 规划中 */
    PLANNING,

    /** 进行中 */
    IN_PROGRESS,

    /** 已暂停 */
    ON_HOLD,

    /** 已完成 */
    COMPLETED,

    /** 已取消 */
    CANCELLED
}
