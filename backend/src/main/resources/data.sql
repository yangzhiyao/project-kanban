-- Dev seed data (recreated on every start because H2 is in-memory with ddl-auto=create-drop)
-- updated_at 刻意错开，保证列表按更新时间倒序时顺序稳定。

insert into project (code, name, description, status, priority, owner, start_date, end_date, progress,
                     receivable_amount, payable_amount, received_amount, paid_amount,
                     deleted, created_at, updated_at)
values ('PRJ-0001', '企业门户网站重构', '统一门户、单点登录与内容管理平台重构', 'IN_PROGRESS', 'HIGH', '张三',
        '2026-01-06', '2026-06-30', 45, 1200000.00, 700000.00, 500000.00, 300000.00,
        false, '2026-01-05 09:00:00', '2026-03-01 09:00:00');

insert into project (code, name, description, status, priority, owner, start_date, end_date, progress,
                     receivable_amount, payable_amount, received_amount, paid_amount,
                     deleted, created_at, updated_at)
values ('PRJ-0002', '移动端 APP 一期', '面向 C 端的移动应用一期功能建设', 'PLANNING', 'MEDIUM', '李四',
        '2026-03-02', '2026-09-30', 5, 800000.00, 500000.00, 0.00, 0.00,
        false, '2026-01-05 09:00:00', '2026-03-02 09:00:00');

insert into project (code, name, description, status, priority, owner, start_date, end_date, progress,
                     receivable_amount, payable_amount, received_amount, paid_amount,
                     deleted, created_at, updated_at)
values ('PRJ-0003', '数据中台建设', '数据采集、治理、资产目录与指标体系建设', 'IN_PROGRESS', 'HIGH', '王五',
        '2025-11-03', '2026-08-31', 62, 3500000.00, 2100000.00, 1500000.00, 900000.00,
        false, '2026-01-05 09:00:00', '2026-03-03 09:00:00');

insert into project (code, name, description, status, priority, owner, start_date, end_date, progress,
                     receivable_amount, payable_amount, received_amount, paid_amount,
                     deleted, created_at, updated_at)
values ('PRJ-0004', '客户关系管理系统升级', 'CRM 版本升级与销售流程优化', 'ON_HOLD', 'MEDIUM', '赵六',
        '2026-02-09', '2026-07-31', 30, 600000.00, 380000.00, 200000.00, 150000.00,
        false, '2026-01-05 09:00:00', '2026-03-04 09:00:00');

insert into project (code, name, description, status, priority, owner, start_date, end_date, progress,
                     receivable_amount, payable_amount, received_amount, paid_amount,
                     deleted, created_at, updated_at)
values ('PRJ-0005', '智能运维监控平台', '统一监控告警与自动化运维', 'COMPLETED', 'HIGH', '孙七',
        '2025-05-06', '2025-12-31', 100, 1500000.00, 900000.00, 1500000.00, 880000.00,
        false, '2026-01-05 09:00:00', '2026-03-05 09:00:00');

insert into project (code, name, description, status, priority, owner, start_date, end_date, progress,
                     receivable_amount, payable_amount, received_amount, paid_amount,
                     deleted, created_at, updated_at)
values ('PRJ-0006', '财务共享中心改造', '费用报销与结算流程线上化', 'PLANNING', 'LOW', '周八',
        '2026-04-01', '2026-12-31', 0, 450000.00, 300000.00, 0.00, 0.00,
        false, '2026-01-05 09:00:00', '2026-03-06 09:00:00');

insert into project (code, name, description, status, priority, owner, start_date, end_date, progress,
                     receivable_amount, payable_amount, received_amount, paid_amount,
                     deleted, created_at, updated_at)
values ('PRJ-0007', '供应链协同系统', '供应商协同与采购订单跟踪（已终止）', 'CANCELLED', 'LOW', '吴九',
        '2026-01-20', '2026-05-31', 15, 700000.00, 420000.00, 100000.00, 60000.00,
        false, '2026-01-05 09:00:00', '2026-03-07 09:00:00');

insert into project (code, name, description, status, priority, owner, start_date, end_date, progress,
                     receivable_amount, payable_amount, received_amount, paid_amount,
                     deleted, created_at, updated_at)
values ('PRJ-0008', '人力资源数字化平台', '招聘、绩效与培训一体化平台', 'IN_PROGRESS', 'MEDIUM', '郑十',
        '2025-09-01', '2026-03-31', 55, 950000.00, 560000.00, 600000.00, 350000.00,
        false, '2026-01-05 09:00:00', '2026-03-08 09:00:00');
