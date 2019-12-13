package com.kakinari.jenkins

class PoundControl extends RemoteAccess  implements Serializable {
    def String commandBase = "/usr/local/sbin/poundctl  -c /var/pound/socket/pound_%s.sock %s %s"
    def PoundInfo info
    def String addOpts
    
    PoundControl(steps, info) {
        super(steps, info)
        this.info = info
        this. addOpts = info.targetName ==~ /[0-9]*\.[0-9]*\.[0-9]*\.[0-9]*/ ? '' :  ' -H '
    }

    def execute(opts) {
        return remsh(String.format(commandBase, info.portNumber,  this.addOpts, opts))
    }

    def executeStatus(opts) {
        return remshStatus(String.format(commandBase, info.portNumber,  this.addOpts, opts))
    }

    def boolean isActive(steps) {
        return (executeStatus(" | grep  ${info.targetName} | grep -q active") == 0)
    }

    def boolean setActive(steps) {
        execute("-B 0 0 ${info.backendID}");
    }

    def boolean setInactive(steps) {
        execute("-b 0 0 ${info.backendID}");
    }

    def waituntil(flag, count = 10, interval = 1000) {
        sleep(interval)
        while(flag != isActive(steps) && count != 0) {
            sleep(interval)
            count--
        }
        return (count > 0)
    }

}
