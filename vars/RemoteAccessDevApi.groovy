import com.kakinari.jenkins.RemoteUser
import com.kakinari.jenkins.RemoteAccess

def call() {
    return new RemoteAccess(new RemoteUser (
        credential:   'f39cac72-640d-451c-a943-fc4884203cee',
        username:     'root',
        hostname:     'dev.api.intra.telsys.co.jp'))
}
