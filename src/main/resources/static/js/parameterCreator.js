/**
 * @author Jelle
 * each variable holds an input for classifier settings that can be called depending on which classifier is requested.
 */

let questionMarkIcon = "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-question-circle\" viewBox=\"0 0 16 16\">\n" +
    "  <path d=\"M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z\"/>\n" +
    "  <path d=\"M5.255 5.786a.237.237 0 0 0 .241.247h.825c.138 0 .248-.113.266-.25.09-.656.54-1.134 1.342-1.134.686 0 1.314.343 1.314 1.168 0 .635-.374.927-.965 1.371-.673.489-1.206 1.06-1.168 1.987l.003.217a.25.25 0 0 0 .25.246h.811a.25.25 0 0 0 .25-.25v-.105c0-.718.273-.927 1.01-1.486.609-.463 1.244-.977 1.244-2.056 0-1.511-1.276-2.241-2.673-2.241-1.267 0-2.655.59-2.75 2.286zm1.557 5.763c0 .533.425.927 1.01.927.609 0 1.028-.394 1.028-.927 0-.552-.42-.94-1.029-.94-.584 0-1.009.388-1.009.94z\"/>\n" +
    "</svg>"

let maximumBatchSize = "<label for=\"batchsize\" class=\"form-label\" th:text=\"\">BatchSize</label><br>\n " +
    "<input name=\"batchsize\" id=\"batchsize\" type=\"number\" value=\"100\"><br>"

let debug = "<label for=\"debug\" class=\"form-label\" th:text=\"\">debug</label><br>\n" +
    "  <select id=\"debug\" name=\"debug\">\n" +
    "    <option th:text=\"\" value=\"true\">True</option>\n" +
    "    <option th:text=\"\" value=\"false\">False</option>\n" +
    "  </select><br>"

let doNotCheckCapabilities = "<label th:text = \"\" class=\"form-label\" for='doNotCheckCapabilities'>doNotCheckCapabilities</label><br>" +
    "  <select id=\"doNotCheckCapabilities\" name=\"doNotCheckCapabilities\">\n" +
    "    <option th:text=\"\" value=\"true\">True</option>\n" +
    "    <option th:text=\"\" value=\"false\">False</option>\n" +
    "  </select><br>"

let numDecimalPlaces = "  <label th:text=\"\" for=\"numDecimalPlaces\">numDecimalPlaces</label><br>\n" +
    "  <input id=\"numDecimalPlaces\" name=\"numDecimalPlaces\" type=\"number\" value=\"2\"><br>"

let minBucketSize = "  <label th:text=\"\" for=\"minBucketSize\">minBucketSize</label><br>\n" +
    "  <input id=\"minBucketSize\" name=\"minBucketSize\" type=\"number\" value=\"6\"><br>"

let confidenceFactor = "  <label th:text=\"\" class=\"form-label\" for=\"confidenceFactor\">confidence factor</label><br>\n" +
    "  <input id=\"confidenceFactor\" name=\"confidenceFactor\" type=\"number\" value=\"0.25\"><br>"

let minNumObj = "  <label th:text=\"\" class=\"form-label\" for=\"minNumObj\">minNumObj</label><br>\n" +
    "  <input id=\"minNumObj\" name=\"minNumObj\" type=\"number\" value=\"2\" min=\"2\"><br>"

let numFolds = "  <label th:text=\"\" class=\"form-label\" for=\"numFolds\">numFolds</label><br>\n" +
    "  <input id=\"numFolds\" name=\"numFolds\" type=\"number\" value=\"3\"><br>"

let pruned = "  <label th:text=\"\" for=\"pruned\" class=\"form-label\">pruned</label><br>\n" +
    "  <select id=\"pruned\" name=\"pruned\">\n" +
    "    <option th:text=\"\" value=\"true\">True</option>\n" +
    "    <option th:text=\"\" value=\"false\">False</option>\n" +
    "  </select><br>"

let laPlace = "  <label th:text=\"\" for=\"laPlace\" class=\"form-label\">pruned</label><br>\n" +
    "  <select id=\"laPlace\" name=\"laPlace\">\n" +
    "    <option th:text=\"\" value=\"true\">True</option>\n" +
    "    <option th:text=\"\" value=\"false\">False</option>\n" +
    "  </select><br>"

let KNN = "  <label th:text=\"\" for=\"KNN\">minBucketSize</label><br>\n" +
    "  <input id=\"KNN\" name=\"KNN\" type=\"number\" value=\"1\" min=\"1\"><br>"

let crossValidate = "  <label th:text=\"\" for=\"crossValidate\" class=\"form-label\">pruned</label><br>\n" +
    "  <select id=\"crossValidate\" name=\"crossValidate\">\n" +
    "    <option th:text=\"\" value=\"true\">True</option>\n" +
    "    <option th:text=\"\" value=\"false\">False</option>\n" +
    "  </select><br>"

let nnSearchAlgorithm = "  <label th:text=\"\" for=\"nnSearchAlgorithm\">nnSearchAlgorithm</label><br>\n" +
    "  <select id=\"nnSearchAlgorithm\" name=\"nnSearchAlgorithm\">\n" +
    "    <option value=\"BallTree\">BallTree</option>\n" +
    "    <option value=\"CoverTree\">CoverTree</option>\n" +
    "    <option value=\"FilteredNeighbourSearch\">FilteredNeighbourSearch</option>\n" +
    "    <option value=\"KDTree\">KDtree</option>\n" +
    "    <option value=\"LinearNNSearch\">LinearNNSearch</option>\n" +
    "  </select><br>"

/**
 * Get parameter inputs for ZeroR classifier
 */
let getZeroR = function (){
    document.querySelector("#ZeroR").innerHTML =
        "<form>" +
        maximumBatchSize +
        debug +
        doNotCheckCapabilities +
        numDecimalPlaces +
        "</form>"
}