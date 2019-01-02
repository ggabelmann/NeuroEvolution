A small project to allow for experimentation with neural networks via evolution.
Inspired by Uber's papers about <a href="https://eng.uber.com/deep-neuroevolution/">Deep Neuroevolution</a>.

The network is basically a DAG -- an array of nodes and an array of edges (that point "from" a node "to" a later node).
The reason I wanted to try a DAG is because that makes it easy to experiment with sparsely-connected networks.
It works but is probably 1000 times slower than multiplying matrices.
Also, matrices with zeroes allow the same thing...
It was fun to program, though.

Biases and Weights are randomly updated and something akin to simulated annealing is used to evolve the network to a good solution.
No backprop necessary!

## Future

At the moment there is little in the way of error-checking or tests.
But I hope to make the code more modular and robust and use it as a platform to experiment on.

## Public Service Announcement

I'm looking for full-time and/or contract work! My [Github Profile](https://ggabelmann.github.com) has more details on my skills, how I like to work, and my resume. Drop me a line and we can chat about working together.
