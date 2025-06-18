@echo off
chcp 65001 >nul
echo =====================================
echo 数据库性能监控脚本
echo =====================================

:: 设置数据库连接参数
set DB_HOST=localhost
set DB_PORT=3306
set DB_NAME=ops_work_order_system
set DB_USER=root

echo 监控数据库：%DB_NAME%
echo 服务器：%DB_HOST%:%DB_PORT%
echo.

:: 检查MySQL客户端
mysql --version >nul 2>&1
if errorlevel 1 (
    echo 错误：未找到MySQL客户端工具
    pause
    exit /b 1
)

:: 提示用户输入数据库密码
set /p DB_PASS=请输入数据库密码：

echo.
echo =====================================
echo 性能监控报告
echo =====================================

:: 创建临时SQL文件
set TEMP_SQL=%TEMP%\monitor_performance.sql
echo -- 数据库性能监控查询 > "%TEMP_SQL%"
echo. >> "%TEMP_SQL%"
echo -- 1. 索引使用情况统计 >> "%TEMP_SQL%"
echo SELECT  >> "%TEMP_SQL%"
echo     'INDEX_STATS' as '报告类型', >> "%TEMP_SQL%"
echo     TABLE_NAME as '表名', >> "%TEMP_SQL%"
echo     COUNT(*) as '索引数量', >> "%TEMP_SQL%"
echo     GROUP_CONCAT(INDEX_NAME) as '索引列表' >> "%TEMP_SQL%"
echo FROM INFORMATION_SCHEMA.STATISTICS  >> "%TEMP_SQL%"
echo WHERE TABLE_SCHEMA = '%DB_NAME%' >> "%TEMP_SQL%"
echo     AND INDEX_NAME LIKE 'idx_%%' >> "%TEMP_SQL%"
echo GROUP BY TABLE_NAME >> "%TEMP_SQL%"
echo ORDER BY COUNT(*) DESC; >> "%TEMP_SQL%"
echo. >> "%TEMP_SQL%"
echo -- 2. 表大小统计 >> "%TEMP_SQL%"
echo SELECT  >> "%TEMP_SQL%"
echo     'TABLE_SIZE' as '报告类型', >> "%TEMP_SQL%"
echo     TABLE_NAME as '表名', >> "%TEMP_SQL%"
echo     TABLE_ROWS as '记录数', >> "%TEMP_SQL%"
echo     ROUND(DATA_LENGTH/1024/1024, 2) as '数据大小MB', >> "%TEMP_SQL%"
echo     ROUND(INDEX_LENGTH/1024/1024, 2) as '索引大小MB' >> "%TEMP_SQL%"
echo FROM INFORMATION_SCHEMA.TABLES >> "%TEMP_SQL%"
echo WHERE TABLE_SCHEMA = '%DB_NAME%' >> "%TEMP_SQL%"
echo ORDER BY DATA_LENGTH DESC; >> "%TEMP_SQL%"
echo. >> "%TEMP_SQL%"
echo -- 3. 查询性能统计（需要开启performance_schema） >> "%TEMP_SQL%"
echo SELECT  >> "%TEMP_SQL%"
echo     'QUERY_STATS' as '报告类型', >> "%TEMP_SQL%"
echo     OBJECT_SCHEMA as '数据库', >> "%TEMP_SQL%"
echo     OBJECT_NAME as '表名', >> "%TEMP_SQL%"
echo     COUNT_READ as '读取次数', >> "%TEMP_SQL%"
echo     COUNT_WRITE as '写入次数', >> "%TEMP_SQL%"
echo     SUM_TIMER_READ as '读取总时间', >> "%TEMP_SQL%"
echo     SUM_TIMER_WRITE as '写入总时间' >> "%TEMP_SQL%"
echo FROM performance_schema.table_io_waits_summary_by_table >> "%TEMP_SQL%"
echo WHERE OBJECT_SCHEMA = '%DB_NAME%' >> "%TEMP_SQL%"
echo ORDER BY COUNT_READ DESC >> "%TEMP_SQL%"
echo LIMIT 10; >> "%TEMP_SQL%"
echo. >> "%TEMP_SQL%"
echo -- 4. 连接和会话统计 >> "%TEMP_SQL%"
echo SELECT  >> "%TEMP_SQL%"
echo     'CONNECTION_STATS' as '报告类型', >> "%TEMP_SQL%"
echo     VARIABLE_NAME as '指标名称', >> "%TEMP_SQL%"
echo     VARIABLE_VALUE as '当前值' >> "%TEMP_SQL%"
echo FROM performance_schema.global_status >> "%TEMP_SQL%"
echo WHERE VARIABLE_NAME IN ( >> "%TEMP_SQL%"
echo     'Connections', >> "%TEMP_SQL%"
echo     'Threads_connected', >> "%TEMP_SQL%"
echo     'Threads_running', >> "%TEMP_SQL%"
echo     'Queries', >> "%TEMP_SQL%"
echo     'Slow_queries', >> "%TEMP_SQL%"
echo     'Uptime' >> "%TEMP_SQL%"
echo ); >> "%TEMP_SQL%"

:: 执行监控查询
echo 正在收集性能数据...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASS% %DB_NAME% < "%TEMP_SQL%"

if errorlevel 1 (
    echo.
    echo 性能监控失败！请检查数据库连接。
    del "%TEMP_SQL%" >nul 2>&1
    pause
    exit /b 1
)

:: 清理临时文件
del "%TEMP_SQL%" >nul 2>&1

echo.
echo =====================================
echo 监控完成！
echo =====================================
echo.
echo 说明：
echo - INDEX_STATS：显示每个表的索引数量和列表
echo - TABLE_SIZE：显示表的记录数和存储大小
echo - QUERY_STATS：显示查询性能统计（需要performance_schema）
echo - CONNECTION_STATS：显示数据库连接和查询统计
echo.
echo 性能优化建议：
echo 1. 关注查询频率高但缺少索引的表
echo 2. 监控慢查询日志
echo 3. 定期检查索引使用情况
echo 4. 优化大表的数据结构
echo.

pause 