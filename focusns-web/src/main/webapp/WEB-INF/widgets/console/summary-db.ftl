<div class="widget">
    <div class="widget-hd">
        <h2>数据库环境</h2>
    </div>
    <div class="widget-bd">
        <div class="summary-db">
            <table>
                <tr>
                    <th>数据库名称</th>
                    <td>${Request.envDB.databaseName!"无..."}</td>
                </tr>
                <tr>
                    <th>数据库版本</th>
                    <td>${Request.envDB.databaseVersion!"无..."}</td>
                </tr>
                <tr>
                    <th >数据库驱动名称</th>
                    <td>${Request.envDB.driverName!"无..."}</td>
                </tr>
                <tr>
                    <th>数据库驱动版本</th>
                    <td>${Request.envDB.driverVersion!"无..."}</td>
                </tr>
                <tr>
                    <th>数据库URL</th>
                    <td>${Request.envDB.url!"无..."}</td>
                </tr>
                <tr>
                    <th>数据库用户名</th>
                    <td>${Request.envDB.username!"无..."}</td>
                </tr>
                <#--
                <tr>
                    <th>数据库当前连接数</th>
                    <td>${Request.envDB.maxConnections!"无..."}</td>
                </tr>
                -->
            </table>
        </div>
    </div>
</div>