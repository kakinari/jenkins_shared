def mysql = null
def getInstance(steps, info) {
    if (mysql == null)
        mysql = new MySQLServer(steps, info)
    return mysql
}