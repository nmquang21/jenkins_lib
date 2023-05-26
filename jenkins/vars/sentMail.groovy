def call(boolean status){
    boolean res = true
    def emailBody = """ """
    try{
        if(status){
            emailBody = """
                <html>
                    <strong>Đã có bản build mới!</strong>
                    <div>Truy cập tại địa chỉ <a href="utc-room.online">utc-room.online</a></div>
                    <div>Tài khoản: <strong>phamtamhg01@gmail.com</strong></div>
                    <div>Mật khẩu: <strong>12345678@Utc</strong></div>
                </html>
            """     
        }
        else{
            emailBody = """
                <html>
                    <strong><font color="#ff000">Đã xảy ra lỗi trong quá trình build!</font></strong>
                </html>
            """   
        }
        
        emailext body : emailBody,
        subject : "Thông bảo bản build utc-room.online",
        mimeType : "text/html",
        to : "nmquang21@gmail.com"
    }catch(ex){
        res = false
        echo ex.toString()
    }
    return res
}
