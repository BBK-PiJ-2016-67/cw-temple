# PiJ Coursework Four

This project implements a solution to help Philip Hammond retrieve the Orb of Lots and escape the Temple of Gloom.

# Exploration

During the exploration phase, a simple algorithm is used to traverse each unvisited branch until the Orb has been reached. The order in which the branches are traversed is determined by the distance of the first node in each branch from the Orb.

With this solution, Philip is always able to reach the node, and he is mostly able to do so using an efficient route. A limitation of this solution is that if Philip reaches a fork which is close in distance to the Orb, and selects a route which has many branches which are each progressively further away from the Orb, each branch will be traversed unnecessarily before Philip returns to the first branch and selects a route which is more likely to reach the Orb.

To resolve this issue each fork could be stored and when Philip reaches a dead end he could go back and traverse a different branch from the fork which is closest to the Orb.

# Escape

During the escape phase, an instance of the Crawler class is used in a similar manner to the method used during exploration. The instance traverses each branch until the exit has been reached. Branches are selected at random to ensure that multiple instances of the Crawler class will find different routes to the exit.

A single instance of the Crawler class will find a route to the exit, but the route may not reach the exit within the given time limit, and it may not retrieve the maximum amount of gold. To resolve this, 2000 instances of the Crawler class are instantiated in a thread pool of 2000 threads. Each instance concurrently finds a different route to the exit.

Once all instances have found a route, any routes that do not complete within the given time limit are filtered out, and the remaining routes are sorted by the amount of gold retrieved. The route that retrieves the most amount of gold is selected, and Philip traverses this route, picking up any gold on the way.

A limitation of this solution is that due to the random nature of the implementation it could happen that none of the routes found by the Crawler class are able to reach the exit within the given time limit. However, in practice I have not yet found a map that cannot be solved by this approach.

# Classes

The implementation for the explore phase can be found in the explore method of the Explorer class.

The escape method of the Explorer class creates a thread pool of instances of the Crawler class, filters and sorts the routes returned by these instances and then traverses the optimal route.

The Crawler class has been added to implement the Runnable interface and return a route between two given nodes.