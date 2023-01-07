# dyn4j-samples
A collection of samples that use the dyn4j library in a variety of ways.

The intent of these samples is to provide a simple framework for building applications with the dyn4j library. The samples show how to use features like joints, static collision detection, raycasting, CCD, and so on. They also provide some creative ways to solve common problems like one-way platforms, jumping, destruction, player control, and so on. That said, none of these are intended to be complete solutions or even the correct solution for your use-case.

* All samples support zoom and pan using the mouse and mouse wheel
* All samples support object manipulation with the mouse
* All samples have features like reset, reset camera, toggle rendering, etc.
* Each sample may have it's own controls - see the console output when running for the full list of controls

| Sample | Description | Concepts |
| --- | --- | --- |
| [BasketBall](https://github.com/dyn4j/dyn4j-samples/tree/master/src/main/java/org/dyn4j/samples/BasketBall.java) | A side view basketball game | Player Input, DistanceJoint, RevoluteJoint, Image Mapping/Texturing, Filtering, Bounds Listening |
| [Billiards](https://github.com/dyn4j/dyn4j-samples/tree/master/src/main/java/org/dyn4j/samples/Billiards.java) | A top down billiards simulation | Density, Friction, Damping, Player Input |
| [Bowling](https://github.com/dyn4j/dyn4j-samples/tree/master/src/main/java/org/dyn4j/samples/Bowling.java) | A side view of a bowling ball hitting pins | Density, Friction, Restitution, CategoryFilter |
| [Bridge](https://github.com/dyn4j/dyn4j-samples/tree/master/src/main/java/org/dyn4j/samples/Bridge.java) | A side view of a bridge made from joined bodies | RevoluteJoint |
| [Bucket](https://github.com/dyn4j/dyn4j-samples/tree/master/src/main/java/org/dyn4j/samples/Bucket.java) | A side view of a bucket with 200 random objects | Larger world |
| [Concave](https://github.com/dyn4j/dyn4j-samples/tree/master/src/main/java/org/dyn4j/samples/Concave.java) | A side view of a concave object | Concave vs. Convex, Convex Composition |
| [Crank](https://github.com/dyn4j/dyn4j-samples/tree/master/src/main/java/org/dyn4j/samples/Crank.java) | A side view of a piston | RevoluteJoint, PrismaticJoint |
| [Decomposition](https://github.com/dyn4j/dyn4j-samples/tree/master/src/main/java/org/dyn4j/samples/Decomposition.java) | A side view of a few simple polygons decomposed | Convex Decomposition |
| [Destructible](https://github.com/dyn4j/dyn4j-samples/tree/master/src/main/java/org/dyn4j/samples/Destructible.java) | A side view of destruction of a body and joint | Remove/Add Buffering |
| [Images](https://github.com/dyn4j/dyn4j-samples/tree/master/src/main/java/org/dyn4j/samples/Images.java) | A side view of a scene where images are mapped to bodies | Image Mapping/Texturing |
| [LinkTerrain](https://github.com/dyn4j/dyn4j-samples/tree/master/src/main/java/org/dyn4j/samples/LinkTerrain.java) | A side view of a link-based floor | Smooth Sliding, Link |
| [Maze](https://github.com/dyn4j/dyn4j-samples/tree/master/src/main/java/org/dyn4j/samples/Maze.java) | A top down view of a maze with a player controled body | MotorJoint, Player Control |
| [Organize](https://github.com/dyn4j/dyn4j-samples/tree/master/src/main/java/org/dyn4j/samples/Organize.java) | A side view of a scene where bodies are randomly joined and self organize | DistanceJoint |
| [Platformer](https://github.com/dyn4j/dyn4j-samples/tree/master/src/main/java/org/dyn4j/samples/Platformer.java) | A side view of a platformer simulation | OnGround, Player Control, One-way Platform, Jumping |
| [Pyramid](https://github.com/dyn4j/dyn4j-samples/tree/master/src/main/java/org/dyn4j/samples/Pyramid.java) | A side view of a Pyramid of stacked boxes | Stacking |
| [Ragdoll](https://github.com/dyn4j/dyn4j-samples/tree/master/src/main/java/org/dyn4j/samples/Ragdoll.java) | A side view of a ragdoll made from joints | RevoluteJoint |
| [Stacking](https://github.com/dyn4j/dyn4j-samples/tree/master/src/main/java/org/dyn4j/samples/Stacking.java) | A side view of a scene where the player can add boxes dynamically | Stacking, Add Bodies |
| [Tank](https://github.com/dyn4j/dyn4j-samples/tree/master/src/main/java/org/dyn4j/samples/Tank.java) | A top down tank simulation | Raycasting, Player Control, FrictionJoint, RevoluteJoint |
| [Thrust](https://github.com/dyn4j/dyn4j-samples/tree/master/src/main/java/org/dyn4j/samples/Thrust.java) | A side view of a scene with a rocket | Applying Forces |
| [Tracking](https://github.com/dyn4j/dyn4j-samples/tree/master/src/main/java/org/dyn4j/samples/Tracking.java) | A scene where contact tracking is printed to the console | Contact Tracking |
| [Truck](https://github.com/dyn4j/dyn4j-samples/tree/master/src/main/java/org/dyn4j/samples/Truck.java) | A scene where a truck filled with boxes moves through a world | WheelJoint, Fixture Composition |
| [UsingGraphics2D](https://github.com/dyn4j/dyn4j-samples/tree/master/src/main/java/org/dyn4j/samples/UsingGraphics2D.java) | A scene with just a bunch of shapes rendered via Java 2D | Java 2D |
| [UsingJogl](https://github.com/dyn4j/dyn4j-samples/tree/master/src/main/java/org/dyn4j/samples/UsingJogl.java) | A scene with a few shapes rendered via OpenGL via JOGL | JOGL |

### BasketBall
Use angle, power, and position to attempt to make baskets. The sample only scores goals, so don't miss! If you are close you get 2 points, far away you get 3.  Only if you get it in when you launch it is counted.

![BasketBall Sample](captures/BasketBall.gif?raw=true "BasketBall sample")

### Billiards
Use the cue stick to hit the cue ball to hit the other balls.  You must wait until the balls settle before your next hit.

![Billiards Sample](captures/Billiards.gif?raw=true "Billiards sample")

### Platformer
Use the ball to "run" around the level and jump. You can jump onto platforms above you no matter where you are, but if you land on them, they are solid. You can drop down from the top level as well. Green means you are touching the ground and can jump. Purple means you are not touching the ground.

![Platformer Sample](captures/Platformer.gif?raw=true "Platformer sample")

### Ragdoll
Use your mouse to drag around and contort the ragdoll

![Ragdoll Sample](captures/Ragdoll.gif?raw=true "Ragdoll sample")

### Tank
Drive a tank! Independently operating the tracks and barrel. The barrel will be pushed aside by obstacles. The barrel is "shooting" a ray to see what it would hit. You can press a key to destroy the closest object you are aiming at.

![Tank Sample](captures/Tank.gif?raw=true "Tank sample")

### Thrust
How hard can it be to fly a rocket? Using only front, back and side thrusters (applied force) fly a rocket around an enclosed area. Challenge yourself to land without tipping over or flying in a smooth trajectory.

![Thrust Sample](captures/Thrust.gif?raw=true "Thrust sample")

### Truck
Drive a truck with cargo! Accelerate and decelerate using the keyboard or stop immediately. Watch the contents slide around as you drive. Use the mouse to manipulate the truck frame to see the suspension compress.

![Truck Sample](captures/Truck.gif?raw=true "Truck sample")

