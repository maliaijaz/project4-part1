// package test;
//
// import java.io.IOException;
//
// import java.io.PrintWriter;
// import java.util.ArrayList;
//
// import javax.servlet.ServletException;
// import javax.servlet.http.HttpServlet;
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;
//
//
// import org.json.simple.JSONArray;
// import org.json.simple.JSONObject;
//
// public class SearchFormServlet extends HttpServlet {
// private int id;
// private DBProc dbp = null;
//
// public SearchFormServlet() {
// dbp = new DBProc();
// }
//
// protected void doGet(HttpServletRequest request, HttpServletResponse
// response) throws ServletException, IOException {
// response.setContentType("application/json");
//
// PrintWriter out = response.getWriter();
//
// String q = request.getParameter("query");
// String cb = request.getParameter("callback");
// if ((q == null) || (q.length() <= 0) || cb == null)
// out.print("");
// else {
// ArrayList<Movie> movies = dbp.getMoviesByTitleForAjax(q);
// JSONArray mArray = new JSONArray();
// for (Movie mv : movies) {
// JSONObject obj = new JSONObject();
// obj.put("title", mv.getTitle());
// obj.put("id", mv.getId());
//
// mArray.add(obj);
// System.out.println(mArray);
// }
// System.out.println("JSON Array: " + mArray.toString());
// // writing the json-array to the output stream
// out.print(cb + "(" + mArray + ");");
//
// }
//
// out.flush();
// }
//
// protected void doPost(HttpServletRequest request, HttpServletResponse
// response) throws ServletException, IOException {
// doGet(request, response);
// }
// }