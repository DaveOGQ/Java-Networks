public class UrlParser {

    private String url;

    public UrlParser(String Url) {
        url = Url;
        //parse the url here, into port, protocol and so on
	}

    /**
     * parses the url for the name of the protocol
     * @return
     */
    public String getProtocol(){
        // split at '://'
        String[] arr = url.split("://");
        String protocol = arr[0];

        return protocol;
    }

    /**
     * parses the url for a port if there is one
     * @return
     */
    public String getPort(){
        String[] arr = url.split("://");
        String potentialport = arr[1];
        String[] check = potentialport.split(":");

        if(check.length ==  2){
            //there is a port
            String portstr = check[1];

            String[] parts = portstr.split("/");

            String port  = parts[0];
            
            return port;
        }

        return "";
    }

    /**
     * parses the url for the name of the host
     * @return
     */
    public String getHostname(){
        String[] arr = url.split("://");
        
        String[] parts = arr[1].split(":", 2);

        if (parts.length == 1){
            parts = arr[1].split("/", 2);
        }

        String hostname = parts[0];

        return hostname;
    }

    /**
     * parses the url for the pathname of the object
     * @return
     */
    public String getPathName(){
        String[] arr = url.split("://");
        
        String[] parts = arr[1].split("/", 2);
        
        String pathname = parts[1];

        return pathname;
    }

    /**
     * parses the pathname for the name file we are trying to download
     * @param path the pathname of the object to be retrieved
     * @return
     */
    public String getFileName(String path){
        String[] paths = path.split("/");
        String fileName = paths[paths.length - 1];
        return fileName;
    }
}
