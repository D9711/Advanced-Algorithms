import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.LinkedList;

public class BipMatch {
    Kattio io;

    int num_vertices;
    int source;
    int sink;
    int num_edges;

    HashMap<Integer, HashMap<Integer, Integer>> flow_graph = new HashMap<>();
    HashMap<Integer, HashMap<Integer, Integer>> res_capacity_graph = new HashMap<>();
    HashMap<Integer, Integer> bfs_result_path = new HashMap<>();

    int bip_edges;
    int bip_x;
    int bip_y;
    HashMap<Integer, HashMap<Integer, Integer>> bip_capacity = new HashMap<>();

    void readFlowGraph() {
        /** BIPARTITE INPUT */

        bip_x = io.getInt();
        bip_y = io.getInt();
        bip_edges = io.getInt();

        source = bip_x + bip_y + 1;
        sink = bip_x + bip_y + 2;

        // Läs in kanterna
        for (int i = 0; i < bip_edges; ++i) {
            int a = io.getInt();
            int b = io.getInt();

            // G: Lägg till kant mellan a och b, samt kapacitetrr till 1.
            if (!bip_capacity.containsKey(a))
                bip_capacity.put(a, new HashMap<>());

            // G: Lägg till kant mellan X och Y, med kapacitet 1.
            bip_capacity.get(a).put(b, 1);

        }
        /** BIPARTITE INPUT */

        num_vertices = bip_x + bip_y + 2;
    }

    void initialize_capacity_graph() {

        // Lägg in alla noder i flödesgrafen och restkapacitetesgrafen.
        for (int i = 1; i <= num_vertices; i++) {
            flow_graph.put(i, new HashMap<>());
            res_capacity_graph.put(i, new HashMap<>());
        }

        // Lägg till kanter mellan alla x-noder och källnod
        for (int i = 1; i <= bip_x; i++) {
            flow_graph.get(source).put(i, 0);
            flow_graph.get(i).put(source, 0);

            res_capacity_graph.get(source).put(i, 1);
            res_capacity_graph.get(i).put(source, 0);
        }

        // Lägg till kanter mellan alla y-noder och sänknod
        for (int i = bip_x + 1; i <= bip_x + bip_y; i++) {
            flow_graph.get(sink).put(i, 0);
            flow_graph.get(i).put(sink, 0);

            res_capacity_graph.get(i).put(sink, 1);
            res_capacity_graph.get(sink).put(i, 0);
        }

        for (Map.Entry<Integer, HashMap<Integer, Integer>> nodes : bip_capacity.entrySet()) {
            Integer u = nodes.getKey();
            HashMap<Integer, Integer> edges = nodes.getValue();
            for (Map.Entry<Integer, Integer> edge : edges.entrySet()) {
                Integer v = edge.getKey();

                flow_graph.get(u).put(v, 0);
                flow_graph.get(v).put(u, 0);

                // u i X och v i Y, så alla kanter u -> v har kapacitet 1.
                res_capacity_graph.get(u).put(v, 1);
                res_capacity_graph.get(v).put(u, 0);

            }
        }
    }

    boolean bfs() {

        bfs_result_path.clear();

        Queue<Integer> q = new LinkedList<>();
        HashMap<Integer, Boolean> visited = new HashMap<>();

        visited.put(source, true);
        q.add(source);

        while (!q.isEmpty()) {
            int curr = q.poll();

            for (Map.Entry<Integer, Integer> neighbour : res_capacity_graph.get(curr).entrySet()) {
                int x = neighbour.getKey();
                int c = neighbour.getValue();

                if (!visited.containsKey(x) && c > 0) {
                    visited.put(x, true);
                    q.add(x);
                    bfs_result_path.put(x, curr);

                    if (x == sink) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    void edmond_karp() {

        // while det finns en stig p från s till t i restflödesgrafen do
        while (bfs()) {
            int bottle_neck = Integer.MAX_VALUE;
            int curr = sink;

            // r:=min(cf[u,v]: (u,v) ingår i p)
            bottle_neck = bottleNeck(curr, source, bottle_neck);

            // for varje kant (u,v) i p do
            updateFlowCapacity(bottle_neck, curr);
          
        }
    }

    int bottleNeck(int curr, int source, int bottle_neck) {

        while (curr != source) {
            int parent = bfs_result_path.get(curr);
            int c = res_capacity_graph.get(parent).get(curr);
            bottle_neck = Math.min(bottle_neck, c);
            curr = parent;
        }

        return bottle_neck;

    }

    void updateFlowCapacity(int bottle_neck, int curr) {


        while (curr != source) {
            int parent = bfs_result_path.get(curr);

            // f[u,v]:=f[u,v]+r; f[v,u]:= -f[u,v]
            flow_graph.get(parent).put(curr, flow_graph.get(parent).get(curr) + bottle_neck);
            flow_graph.get(curr).put(parent, flow_graph.get(curr).get(parent) - bottle_neck);

            // cf[u,v]:=c[u,v] - f[u,v]; cf[v,u]:=c[v,u] - f[v,u]
            res_capacity_graph.get(parent).put(curr, res_capacity_graph.get(parent).get(curr) - bottle_neck);
            res_capacity_graph.get(curr).put(parent, res_capacity_graph.get(curr).get(parent) + bottle_neck);

            curr = parent;
        }

    }

    void writeFlowSolutionToMatch() {
        io.println(bip_x + " " + bip_y);

        int total_flow = 0;
        // Totala flödet genom grafen kan inte vara större än totala flödet ut från
        // källnoden
        for (Map.Entry<Integer, Integer> source_edge : flow_graph.get(source).entrySet()) {
            total_flow += source_edge.getValue();
        }

        io.println(total_flow);

        for (Map.Entry<Integer, HashMap<Integer, Integer>> vertices : flow_graph.entrySet()) {
            Integer u = vertices.getKey();
            HashMap<Integer, Integer> edges = vertices.getValue();
            for (Map.Entry<Integer, Integer> edge : edges.entrySet()) {
                Integer v = edge.getKey();
                Integer flow_u_v = edge.getValue();
                if ((flow_u_v == 1) && ((u <= bip_x) && (u >= 1)) && ((v <= bip_y + bip_x) && (v >= bip_x))) {
                    io.println(u + " " + v);
                }
            }
        }
        io.flush();
    }

    BipMatch() {
        io = new Kattio(System.in, System.out);
        readFlowGraph();
        initialize_capacity_graph();
        edmond_karp();
        writeFlowSolutionToMatch();
        io.close();
    }

    public static void main(String[] args) {
        new BipMatch();
    }
}
