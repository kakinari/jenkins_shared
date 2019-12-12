import com.kakinari.jenkins.RemoteUser

def call() {
    return new RemoteUser(
        credential:   'f39cac72-640d-451c-a943-fc4884203cee',
        username:     'root',
        hostname:     'dev.api.intra.telsys.co.jp')
}