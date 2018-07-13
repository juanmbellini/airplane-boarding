# Airplane-Boarding [![Build Status](https://travis-ci.org/juanmbellini/airplane-boarding.svg?branch=master)](https://travis-ci.org/juanmbellini/airplane-boarding)

Final System Simulations project: An airplane boarding simulation

## Getting started

These instructions will install the system in your local machine.

### Prerequisites

1. Clone the repository, or download source code

	```
	$ git clone https://github.com/juanmbellini/airplane-boarding.git
	```
	or

	```
	$ wget https://github.com/juanmbellini/airplane-boarding/archive/master.zip
	```

2. Install Maven, if you haven't yet

    #### Mac OS X

    ```
    $ brew install maven
    ```

    #### Ubuntu

    ```
    $ sudo apt-get install maven
    ```

    #### Other OSes
    Check [Maven website](https://maven.apache.org/install.html).


### Installing

1. Change working directory to project root (i.e where pom.xml is located):

    ```
    $ cd <project-root>
    ```

2. Let maven resolve dependencies:

    ```
    $ mvn dependency:resolve -U
    ```

3. Create jar file

    ```
    $ mvn clean package
    ```
    **Note:** The jar file will be under ``` <project-root>/target ```


## Usage

You can run the simulation with the following command:


```
$ java -jar <path-to-jar> [arguments]
```

### Amount of rows

You can indicate how many amount of rows there are in the airplane with the ```--custom.system.airplane.rows``` command.
For example, 
```
$ java -jar <path-to-jar> [arguments] --custom.system.airplane.rows=50
```

**The default is 28**


### Amount of seats per side

You can indicate how many seats there are in each side of the airplane with the ```--custom.system.airplane.columns``` command.
For example, 
```
$ java -jar <path-to-jar> [arguments] --custom.system.airplane.columns=2
```

**The default is 3**


### Central hall width

You can set the central hall width with the ```--custom.system.airplane.central-hall-width``` command.
For example, 
```
$ java -jar <path-to-jar> [arguments] --custom.system.airplane.central-hall-width=1
```

**The default is 0.85**

### Front hall length

You can set the front hall width with the ```--custom.system.airplane.front-hall-length``` command.
For example, 
```
$ java -jar <path-to-jar> [arguments] --custom.system.airplane.front-hall-length=1.5
```

**The default is 2**


### Seats width

You can set the seats width with the ```--custom.system.airplane.seat-width``` command.
For example, 
```
$ java -jar <path-to-jar> [arguments] --custom.system.airplane.seat-width=0.75
```

**The default is 0.5**


### Seats separation

You can set the seats separation with the ```--custom.system.airplane.seat-separation``` command.
For example, 
```
$ java -jar <path-to-jar> [arguments] --custom.system.airplane.seat-separation=0.95
```

**The default is 0.7**


### Door length

You can set the door length with the ```--custom.system.airplane.door-length``` command.
For example, 
```
$ java -jar <path-to-jar> [arguments] --custom.system.airplane.door-length=1.2
```

**The default is 1**


### Jet bridge width

You can set the jet bridge width with the ```--custom.system.jet-bridge.width``` command.
For example, 
```
$ java -jar <path-to-jar> [arguments] --custom.system.jet-bridge.width=1.3
```

**The default is 1**


### Jet bridge length

You can set the jet bridge length with the ```--custom.system.jet-bridge.length``` command.
For example, 
```
$ java -jar <path-to-jar> [arguments] --custom.system.jet-bridge.length=18
```
**The default is 28**


### Min. Radius

You can set the particle's min radius with the ```--custom.system.particle.min-radius``` command.
For example, 
```
$ java -jar <path-to-jar> [arguments] --custom.system.particle.min-radius=0.8
```
**There is no default**

### Max. Radius

You can set the particle's max radius with the ```--custom.system.particle.max-radius``` command.
For example, 
```
$ java -jar <path-to-jar> [arguments] --custom.system.particle.max-radius=0.25
```
**There is no default**


### Tao for Contractile Particle model

You can set the Tao for the Contractile Particle models' equations with the ```--custom.system.particle.tao``` command.
For example, 
```
$ java -jar <path-to-jar> [arguments] --custom.system.particle.tao=0.5
```
**There is no default**


### Beta for Contractile Particle model

You can set the Beta for the Contractile Particle models' equations with the ```--custom.system.particle.beta``` command.
For example, 
```
$ java -jar <path-to-jar> [arguments] --custom.system.particle.beta=0.9
```
**There is no default**

### Max. speed

You can set the max. speed for the Contractile Particle models' equations with the ```--custom.system.particle.max-speed``` command.
For example, 
```
$ java -jar <path-to-jar> [arguments] --custom.system.particle.max-speed=1.2
```
**There is no default**

### Time step

You can set the time step for the simulation with the with the ```--custom.system.particle.time-step``` command.
For example, 
```
$ java -jar <path-to-jar> [arguments] --custom.simulation.time-step=0.01
```
**There is no default**

### Boarding strategy

You can set the boarding strategy with the with the ```--custom.simulation.boarding-strategy``` command.
The two possible ways of using this are:
```
$ java -jar <path-to-jar> [arguments] --custom.simulation.boarding-strategy=OUTSIDE_IN
```
or
```
$ java -jar <path-to-jar> [arguments] --custom.simulation.boarding-strategy=BACK_TO_FRONT
```
**There is no default**


### Entry batch

You can set the amount of particle that enter the airplane at the same time with the with the ```--custom.simulation.entry-batch``` command.
For example, 
```
$ java -jar <path-to-jar> [arguments] --custom.simulation.entry-batch=50
```
**There is no default**

### Results output

You can set the Ovito and Octave files using the ```--custom.output.ovito``` and ```--custom.output.octave``` commands.
For example,
```
$ java -jar <path-to-jar> [arguments] --custom.output.ovito=/tmp/ovito.xyx --custom.output.octave=/tmp/octave.m
```
**There is no default**



## Acknowledgement
This is a fork of the [Exit-Room](https://github.com/juanmbellini/exit-room) project.

## Authors

- [Juan Marcos Bellini](https://github.com/juanmbellini)
- [Matías Fraga](https://github.com/matifraga)
