package GsonDefinitions;

import java.util.List;

/**
 * @author michael.hug@fiserv.com
 * Fiserv Internal Software
 */
public class CFAPIResponse {
    public int total_results;
    public int total_pages;
    public String prev_url;
    public String next_url;
    public List<resource> resources;
    
}
