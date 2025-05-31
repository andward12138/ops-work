@echo off
echo 正在启动运维工单系统...
cd ops-work-order-system
mvn spring-boot:run
if errorlevel 1 (
    echo.
    echo 应用启动失败！请检查错误信息。
    pause
) 