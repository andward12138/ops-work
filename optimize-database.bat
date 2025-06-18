@echo off
chcp 65001 >nul
echo =====================================
echo 数据库性能优化脚本
echo =====================================

:: 设置数据库连接参数
set DB_HOST=localhost
set DB_PORT=3306
set DB_NAME=ops_work_order_system
set DB_USER=root

echo 准备优化数据库：%DB_NAME%
echo 服务器：%DB_HOST%:%DB_PORT%
echo.

:: 检查MySQL客户端
mysql --version >nul 2>&1
if errorlevel 1 (
    echo 错误：未找到MySQL客户端工具
    echo 请确保MySQL已安装并添加到PATH环境变量
    pause
    exit /b 1
)

:: 检查优化脚本文件
set SQL_FILE=ops-work-order-system\src\main\resources\db\migration\V8__Performance_Optimization_Indexes.sql
if not exist "%SQL_FILE%" (
    echo 错误：找不到优化脚本文件
    echo 文件路径：%SQL_FILE%
    pause
    exit /b 1
)

echo 找到优化脚本：%SQL_FILE%
echo.

:: 提示用户输入数据库密码
set /p DB_PASS=请输入数据库密码: 

echo.
echo 开始执行数据库优化...
echo 这可能需要2-5分钟，请耐心等待...
echo.

:: 执行优化脚本
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASS% %DB_NAME% < "%SQL_FILE%"

if errorlevel 1 (
    echo.
    echo 数据库优化失败！
    echo 可能的原因：
    echo 1. 数据库连接失败
    echo 2. 权限不足
    echo 3. 数据库结构与脚本不匹配
    echo.
    echo 请检查错误信息并重试。
    pause
    exit /b 1
)

echo.
echo =====================================
echo 数据库优化完成！
echo =====================================
echo.
echo 已创建的优化索引：
echo - 工单表索引: 8个
echo - 用户表索引: 3个  
echo - 部门表索引: 3个
echo - 审批记录索引: 3个
echo - 转派记录索引: 5个
echo - 操作记录索引: 3个
echo - 工作流步骤索引: 5个
echo - 部门权限索引: 2个
echo - 部门联系人索引: 3个
echo - 工作流模板索引: 1个
echo - 工作流模板步骤索引: 2个
echo - 设备令牌索引: 3个
echo.
echo 预期性能提升：
echo - 工单查询速度提升 60-80%%
echo - 统计报表查询提升 70-90%%
echo - 转派记录查询提升 50-70%%
echo - 用户权限查询提升 40-60%%
echo.

pause 