# VLSMCalculator

This program automates the process of creating VLSM subnets (VLSM calculator).

It takes IPv4 network address in CIDR format e.g. x.x.x.x/x, number of subnets to create and parameters for each subnet (size, and choice of minimum/maximum/balanced).

- minimum means subnet will be as large as is minimally necessary)
- balanaced (bal) will atttemp to allocate as much extra addressing as possible for subnets with this mode.
- max results in no remaining addresses after subnetting is finished (subnet will be as large as possible,  in case more than one subnet has this parameter, subnets will fall over into balanced mode)

![alt tag](https://raw.githubusercontent.com/belavina/VLSMCalculator-/master/Sample.png)

You can play around with the program by downloading this jar file : [VLSMCalculator-](https://github.com/belavina/VLSMCalculator-/blob/master/VLSMCalculator.jar?raw=true)
