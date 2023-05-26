def call(String version){
    boolean res = true
    try{
        def textVersion = '1.0'
        def emailBody = """
            <html>
                <strong>demo mail</strong>
            </html>
        """
        emailext body : emailBody,
        subject : "Thông bảo bản build utc-room.online",
        to : "nmquang21@gmail.com"
    }catch(ex){
        res = false
        echo ex.toString()
    }
    return res
}
