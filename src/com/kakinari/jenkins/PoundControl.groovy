package com.kakinari.jenkins

class PoundControl extends RemoteAccess  implements Serializable {
    def String commandBase = "/usr/local/sbin/poundctl  -c /var/pound/socket/pound_%s.sock %s %s"
    def PoundInfo info
    def String addOpts
    
    PoundControl(info) {
        super(info)
        this.info = info
        this. addOpts = info.targetName ==~ /[0-9]*\.[0-9]*\.[0-9]*\.[0-9]*/ ? '' :  ' -H '
    }

    def execute(steps, opts) {
        return remsh(steps, String.format(commandBase, info.portNumber,  this.addOpts, opts))
    }

    def executeStatus(steps, opts) {
        return remshStatus(steps, String.format(commandBase, info.portNumber,  this.addOpts, opts))
    }

    def boolean isActive(steps) {
        return (executeStatus(steps, " | grep  ${info.targetName} | grep -q active") == 0)
    }

    def boolean setActive(steps) {
        execute(steps, "-B 0 0 ${info.backendID}");
    }

    def boolean setInactive(steps) {
        execute(steps, "-b 0 0 ${info.backendID}");
    }

    def waituntil(steps, flag, count = 10, interval = 1000) {
        sleep(interval)
        while(flag != isActive(steps) && count != 0) {
            sleep(interval)
            count--
        }
        return (count > 0)
    }

}
