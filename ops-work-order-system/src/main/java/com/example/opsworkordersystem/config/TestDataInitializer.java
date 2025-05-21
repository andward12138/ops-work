package com.example.opsworkordersystem.config;

import com.example.opsworkordersystem.entity.*;
import com.example.opsworkordersystem.repository.ApprovalRecordRepository;
import com.example.opsworkordersystem.repository.OperationRecordRepository;
import com.example.opsworkordersystem.repository.UserRepository;
import com.example.opsworkordersystem.repository.WorkOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Configuration
public class TestDataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private ApprovalRecordRepository approvalRecordRepository;

    @Autowired
    private OperationRecordRepository operationRecordRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    @Order(2) // 在DataInitializer之后运行
    public CommandLineRunner initTestData() {
        return args -> {
            // 检查是否已存在数据，避免重复初始化
            if (workOrderRepository.count() > 0) {
                System.out.println("测试数据已存在，跳过初始化");
                return;
            }

            System.out.println("开始初始化测试数据...");

            // 获取或创建用户
            User admin = getOrCreateUser("admin", Role.ADMIN);
            User operator = getOrCreateUser("operator", Role.OPERATOR);
            User manager = getOrCreateUser("manager", Role.MANAGER);

            // 创建测试工单
            List<WorkOrder> workOrders = createTestWorkOrders(admin, operator);

            // 创建审批记录
            createApprovalRecords(workOrders, manager);

            // 创建操作记录
            createOperationRecords(workOrders, operator);

            System.out.println("测试数据初始化完成");
        };
    }

    private User getOrCreateUser(String username, Role role) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            return userOpt.get();
        }

        // 如果不存在则创建
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(username + "123"));
        user.setRole(role);
        user.setEmail(username + "@example.com");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    private List<WorkOrder> createTestWorkOrders(User admin, User operator) {
        // 创建不同状态的工单
        WorkOrder pendingOrder = createWorkOrder(
                "系统维护工单", 
                "需要对生产环境进行定期维护，包括系统更新和安全补丁安装", 
                Priority.MEDIUM, 
                Status.PENDING, 
                admin, 
                operator, 
                "运维部");

        WorkOrder inProgressOrder = createWorkOrder(
                "数据库优化工单", 
                "对主数据库进行性能优化，解决查询慢的问题", 
                Priority.HIGH, 
                Status.IN_PROGRESS, 
                admin, 
                operator, 
                "数据库部");

        WorkOrder completedOrder = createWorkOrder(
                "网络故障排查工单", 
                "解决办公区域网络连接不稳定的问题", 
                Priority.HIGH, 
                Status.COMPLETED, 
                admin, 
                operator, 
                "网络部");

        WorkOrder rejectedOrder = createWorkOrder(
                "软件许可证更新工单", 
                "更新过期的软件许可证", 
                Priority.LOW, 
                Status.REJECTED, 
                admin, 
                operator, 
                "资产部");

        WorkOrder overdueOrder = createWorkOrder(
                "服务器迁移工单", 
                "将应用服务器迁移到新的机房", 
                Priority.MEDIUM, 
                Status.OVERDUE, 
                admin, 
                operator, 
                "基础设施部");

        return Arrays.asList(pendingOrder, inProgressOrder, completedOrder, rejectedOrder, overdueOrder);
    }

    private WorkOrder createWorkOrder(
            String title, 
            String description, 
            Priority priority, 
            Status status, 
            User createdBy, 
            User assignedTo, 
            String department) {
        
        WorkOrder workOrder = new WorkOrder();
        workOrder.setTitle(title);
        workOrder.setDescription(description);
        workOrder.setPriority(priority);
        workOrder.setStatus(status);
        workOrder.setCreatedBy(createdBy);
        workOrder.setAssignedTo(assignedTo);
        workOrder.setDepartment(department);
        workOrder.setCreatedAt(LocalDateTime.now().minusDays((long) (Math.random() * 30)));
        workOrder.setUpdatedAt(LocalDateTime.now().minusHours((long) (Math.random() * 24)));
        
        return workOrderRepository.save(workOrder);
    }

    private void createApprovalRecords(List<WorkOrder> workOrders, User manager) {
        // 为每个非待处理状态的工单创建审批记录
        workOrders.stream()
                .filter(wo -> wo.getStatus() != Status.PENDING)
                .forEach(workOrder -> {
                    ApprovalRecord record = new ApprovalRecord();
                    record.setWorkOrder(workOrder);
                    record.setApprover(manager);
                    
                    // 根据工单状态设置审批状态
                    if (workOrder.getStatus() == Status.REJECTED) {
                        record.setStatus(ApprovalStatus.REJECTED);
                        record.setComments("该工单不符合公司流程，请重新提交");
                    } else if (workOrder.getStatus() == Status.COMPLETED || workOrder.getStatus() == Status.IN_PROGRESS) {
                        record.setStatus(ApprovalStatus.APPROVED);
                        record.setComments("审批通过，请按计划执行");
                    } else {
                        record.setStatus(ApprovalStatus.PENDING);
                        record.setComments("待审批");
                    }
                    
                    record.setCreatedAt(workOrder.getCreatedAt().plusHours(2));
                    approvalRecordRepository.save(record);
                });
    }

    private void createOperationRecords(List<WorkOrder> workOrders, User operator) {
        // 为处理中和已完成的工单创建操作记录
        workOrders.stream()
                .filter(wo -> wo.getStatus() == Status.IN_PROGRESS || wo.getStatus() == Status.COMPLETED)
                .forEach(workOrder -> {
                    // 创建执行记录
                    OperationRecord executeRecord = new OperationRecord();
                    executeRecord.setWorkOrder(workOrder);
                    executeRecord.setOperator(operator);
                    executeRecord.setAction(ActionType.EXECUTION);
                    executeRecord.setComments("已开始执行工单任务");
                    executeRecord.setCreatedAt(workOrder.getCreatedAt().plusHours(4));
                    operationRecordRepository.save(executeRecord);
                    
                    // 如果是已完成状态，再添加一条完成记录
                    if (workOrder.getStatus() == Status.COMPLETED) {
                        OperationRecord completeRecord = new OperationRecord();
                        completeRecord.setWorkOrder(workOrder);
                        completeRecord.setOperator(operator);
                        completeRecord.setAction(ActionType.COMPLETION);
                        completeRecord.setComments("工单任务已完成，问题已解决");
                        completeRecord.setCreatedAt(workOrder.getCreatedAt().plusHours(8));
                        operationRecordRepository.save(completeRecord);
                    }
                });
    }
} 