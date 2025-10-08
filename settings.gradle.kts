rootProject.name = "server"

include("domain")
include("domain:model")
include("domain:repository")
include("domain:service")
include("domain:messaging")
include("domain:io")
include("inter")
include("infrastructure")
include("infrastructure:database")
include("infrastructure:messaging")