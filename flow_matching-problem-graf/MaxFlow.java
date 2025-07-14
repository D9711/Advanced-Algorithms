/**
 * Uppgift 2.
 */


import java.util.HashMap;
import java.util.Map;

import java.util.Queue;
import java.util.LinkedList;
import java.util.List;


class MaxFlow {
    Kattio io;

    int num_vertices;
    int source;
    int sink;
    int num_edges;

	HashMap<Integer, HashMap<Integer, Integer>> f_graph = new HashMap<>();
    HashMap<Integer, HashMap<Integer, Integer>> c_graph = new HashMap<>();
    HashMap<Integer, HashMap<Integer, Integer>> cf_graph = new HashMap<>();

    void readFlowGraph() {
        num_vertices = io.getInt();
        source = io.getInt();
        sink = io.getInt();
        num_edges = io.getInt();
    }

    void f_c_cf() {
        for(int i=0; i<num_edges; i++) {
            int u = io.getInt();
            int v = io.getInt();
            int c_u_v = io.getInt();

            if (!c_graph.containsKey(u)) {
                c_graph.put(v, new HashMap<>());
                c_graph.put(u, new HashMap<>());
                f_graph.put(v, new HashMap<>());
                f_graph.put(u, new HashMap<>());
                cf_graph.put(v, new HashMap<>());
                cf_graph.put(u, new HashMap<>());
            }      

            f_graph.get(u).put(v, 0);
            c_graph.get(u).put(v, c_u_v);
            cf_graph.get(u).put(v, c_u_v);
        }
    }


    
    // Vi använder BFS för att hitta kortaste vägen från source till sink
    HashMap<Integer, Integer> bfs() {

        Queue<Integer> q = new LinkedList<>();

        boolean[] visited = new boolean[num_vertices];

        HashMap<Integer, Integer> parent = new HashMap<>();

        visited[source] = true;
    

        q.add(source);

        while(!q.isEmpty()) {

            int curr = q.poll();
            
            for(Map.Entry<Integer, Integer> neighbour: cf_graph.get(curr).entrySet()) {
                int x = neighbour.getKey();
                int c = neighbour.getValue();

                if (!visited[x] && c > 0) {
                    visited[x] = true;
                    q.add(x);
                    parent.put(x, curr);

                    if (x == sink) {
                        return parent;
                    }
                }
            }
            
        }

        return parent;
    }

    void edmond_karp() {

        HashMap<Integer, Integer> p = bfs();

        while (p.containsKey(sink)) {
            
            int bottle_neck = 1000000;

            for(Map.Entry<Integer, Integer> edges: p.entrySet()) {
                Integer child = edges.getKey();
                Integer parent = edges.getValue();
                Integer c = cf_graph.get(parent).get(child);

                if (c < bottle_neck) {
                    bottle_neck = c;
                }
            }

            for(Map.Entry<Integer, Integer> edges: p.entrySet()) {
                Integer child = edges.getKey();
                Integer parent = edges.getValue();
                Integer c = cf_graph.get(parent).get(child);

                Integer curr_flow = f_graph.get(parent).get(child);
                Integer new_flow = curr_flow + bottle_neck;
                f_graph.get(parent).put(child, new_flow);
                f_graph.get(child).put(parent, -new_flow);

                cf_graph.get(parent).put(child, c_graph.get(parent).get(child) - new_flow);
                cf_graph.get(child).put(parent, c_graph.get(child).get(parent) + new_flow);
            
            }

            p = bfs();
        }
    }

    void writeFlowSolution() {
        io.println(num_vertices);
        
        int tot_flow = 0;
        for(Map.Entry<Integer, Integer> sink_edge: f_graph.get(sink).entrySet()) {
            tot_flow += sink_edge.getValue();
        }
        io.println(source + " " + sink + " " + tot_flow);

        int num_pos_flow_edges_zero = 0;
        
        for(Map.Entry<Integer, HashMap<Integer, Integer>> vertices: f_graph.entrySet()) {
            Integer u = vertices.getKey();
            HashMap<Integer, Integer> edges = vertices.getValue();
            for(Map.Entry<Integer, Integer> edge: edges.entrySet()) {
                Integer v = edge.getKey();
                Integer flow_u_v = edge.getValue();
                if (flow_u_v > 0) {
                    num_pos_flow_edges_zero++;
                }
            }
        }
        io.println(num_pos_flow_edges_zero);

        for(Map.Entry<Integer, HashMap<Integer, Integer>> vertices: f_graph.entrySet()) {
            Integer u = vertices.getKey();
            HashMap<Integer, Integer> edges = vertices.getValue();
            for(Map.Entry<Integer, Integer> edge: edges.entrySet()) {
                Integer v = edge.getKey();
                Integer flow_u_v = edge.getValue();
                if (flow_u_v > 0) {
                    io.println(u + " " + v + " " + flow_u_v);
                }
            }
        }


    }

}