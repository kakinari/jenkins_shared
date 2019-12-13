import com.kakinari.jenkins.RemoteUser
import com.kakinari.jenkins.RemoteAccess

def call(steps) {
    return new RemoteAccess(steps, new RemoteUser (
        credential:   'f39cac72-640d-451c-a943-fc4884203cee',
        username:     'root',
        hostname:     'dev.api.intra.telsys.co.jp'))
}
