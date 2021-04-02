# SettlersClone

A fun side project to create a Settlers II clone in Java. Written by myself for myself.

![Demo](gameplay.gif)


## Gameplay
You start with a storehouse, which has some resources in it. 
You build houses by selecting a house in the ui, and clicking on a free spot("O" on Mouseover).
You connect houses by clicking on their flag,  and then clicking on the flag you want to build a road too.
The carriers(monkeys) will begin to carry resources to the construction site.
Once a building is finished, a worker will appear at the storehouse, and go to the house. He will then start working.

### Houses
#### Woodcutter
Cuts down trees and produces logs.
#### Rockcutter
Cuts rocks and produces stone.
#### Forrester
Plants new trees. Trees have to grow before they can be cut down.

#### Sawmill
Takes logs and creates planks.

#### Storehouse
Stores resources produced by workers.

## Controls
Click a house button, and then click on a free spot on the map to build it. Medium buildings require more space around them to be built.

Click on a tile to select it, and then click on another tile, to build a road. Roads start and end with a flag, but flags con only be built at least two tiles apart from each other.

Double right-click on a road or flag to destroy it.

Click on a house to see what resources are in its inventory, where they will be sent, and what resources are on their way.

Use the scroll wheel to zoom.

WASD to move the camera.

F to toggle fullscreen.

## Bugs:
* Buildings can be placed on Water(easy fix).

* If a resource is lying at the flag of a building, and is then assigned to that building, there is no way to get the resource inside. Such a reassign might happen when a road is destroyed or built. This happens because carriers only carry reources from a to b, and not a to a. The best solution would be to have carriers enter buildings to pickup and drop resources.

* There are a number of issues where the assignment of resources goes wrong. This might lead to workers keeping the resource texture. The current fix is to clear any resource textures with every pickup and dropoff.

## Todo:
* Use Java FX scene builder to improve the GUI.

* Allow the game to be resized with the window. This should be easy enough, but I don't know how.

* Improve zooming functionality. The problem here is that I cannot get the viewport to center on a coordinate properly, and instead only set the upper X and Y coordinates. Ideally, I would like the zoom to focus on wherever I have the mouse. 

* Add functionality to see where resources are going, and to direct them somewhere.

* It is difficult to understand what is selected and whether I am in construction mode or not.

* Houses should need a construction worker to be built, and should require time to be finished once the construction resources have arrived. For this, a progress bar should be displayed.

* There should be a saving and loading functionality.

* Carriers should go inside buildings to drop of their resources.

* Flagtiles and houses should only be able to have six resources at a time. I'm not fixing this ATM, because this might cause congested roads to deadlock.

* Currently , a mouseover text "x,o,O,|" is used to indicate what buildings can be built where. This is very unclear. 

* Add more houses. Start with farms and mines.


## Code
The code uses the FXGL game engine.
https://github.com/AlmasB/FXGL

Maven is used to build the project and download the libraries.

## Assets 
Worker textures made by Freepik from www.flaticon.com
https://www.flaticon.com/de/packs/pirates-28

Houses, trees and rocks made by Kenny,  www.kenney.nl,
downloaded at https://opengameart.org/content/hexagon-tiles-93x