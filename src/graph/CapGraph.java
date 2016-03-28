/**
 *
 */
package graph;

import java.util.*;

/**
 * @author Aaron Nwabuoku.
 *         <p>
 *         For the warm up assignment, you must implement your Graph in a class
 *         named CapGraph.  Here is the stub file.
 */
public class CapGraph implements Graph {

    private Map<Integer, HashSet<Integer>> map = new HashMap<>();

    /* (non-Javadoc)
     * @see graph.Graph#addVertex(int)
     */
    @Override
    public void addVertex(int num) {
        if (!map.containsKey(num))
            map.put(num, new HashSet<>());
    }

    /* (non-Javadoc)
     * @see graph.Graph#addEdge(int, int)
     */
    @Override
    public void addEdge(int from, int to) {
        map.get(from).add(to);
    }

    /* (non-Javadoc)
     * @see graph.Graph#getEgonet(int)
     */
    @Override
    public Graph getEgonet(int center) {
        Graph egoNet = new CapGraph();
        egoNet.addVertex(center);
        HashSet<Integer> neighbors = map.get(center);
        for (Integer node : neighbors) {
            egoNet.addVertex(node);
            egoNet.addEdge(center, node);
            map.get(node).stream().filter(neighbors::contains).forEach(nodeNeighbour -> {
                egoNet.addVertex(nodeNeighbour);
                egoNet.addEdge(node, nodeNeighbour);
            });
        }

        return egoNet;
    }

    /* (non-Javadoc)
     * @see graph.Graph#getSCCs()
     */
    @Override
    public List<Graph> getSCCs() {

        List<Graph> SCCs = new ArrayList<>();
        Stack<Integer> vertices = new Stack<>();
        Set<Integer> visited = new HashSet<>();
        Map<Integer, Integer> lowLink = new HashMap<>();
        Counter counter = new Counter();

        map.keySet().stream().filter(currNode ->
                !visited.contains(currNode))
                .forEach(currNode -> tarjanSCC(currNode, SCCs, vertices, visited, lowLink, counter));

        return SCCs;
    }

    private void tarjanSCC(Integer currNode, List<Graph> SCCs, Stack<Integer> vertices, Set<Integer> visited,
                           Map<Integer, Integer> lowLink, Counter counter) {
        lowLink.put(currNode, counter.increment());
        visited.add(currNode);
        vertices.push(currNode);
        boolean isRoot = true;

        for (Integer neighbour : map.get(currNode)) {
            if (!visited.contains(neighbour))
                tarjanSCC(neighbour, SCCs, vertices, visited, lowLink, counter);
            if (lowLink.get(currNode) > lowLink.get(neighbour)) {
                lowLink.put(currNode, lowLink.get(neighbour));
                isRoot = false;
            }
        }

        if (isRoot) {
            Graph component = new CapGraph();

            Integer node;
            do {
                node = vertices.pop();
                component.addVertex(node);
                lowLink.put(node, Integer.MAX_VALUE);
            } while (!currNode.equals(node));

            SCCs.add(component);
        }
    }

    /* (non-Javadoc)
     * @see graph.Graph#exportGraph()
     */
    @Override
    public HashMap<Integer, HashSet<Integer>> exportGraph() {
        return new HashMap<>(map);
    }

    private static class Counter {
        private int c = 0;

        int increment() {
            return c++;
        }
    }

}
