/*
 * package com.rs.game.player.web;
 * 
 * import java.io.*; import java.net.InetSocketAddress; import
 * java.nio.file.Files; import java.nio.file.Paths; import java.util.Scanner;
 * import com.sun.net.httpserver.*; import com.rs.game.World; import
 * com.rs.game.player.Player; import
 * com.rs.game.player.actions.Woodcutting.TreeDefinitions; import
 * com.rs.game.player.actions.automation.AutoSkillingManager; import
 * com.rs.game.player.actions.automation.AutoSkillingManager.InventoryAction;
 * import com.rs.utils.Logger; import org.json.simple.JSONObject; import
 * org.json.simple.parser.JSONParser;
 * 
 * public class AutoSkillingWebServer {
 * 
 * private HttpServer server; private final int port;
 * 
 * public AutoSkillingWebServer(int port) { this.port = port; }
 * 
 * public void start() throws IOException { server = HttpServer.create(new
 * InetSocketAddress(port), 0);
 * 
 * // Remove /web/ prefix from all contexts
 * server.createContext("/autoskilling", new MainPageHandler());
 * server.createContext("/api/auth", new AuthHandler());
 * server.createContext("/api/start", new StartHandler());
 * server.createContext("/api/stop", new StopHandler());
 * server.createContext("/api/status", new StatusHandler());
 * 
 * server.setExecutor(null); server.start();
 * 
 * Logger.log("WebServer", "Auto Skilling web interface started on port " +
 * port); Logger.log("WebServer", "Access at: http://localhost:" + port +
 * "/autoskilling"); }
 * 
 * public void stop() { if (server != null) { server.stop(0); } }
 * 
 * // Serve the main HTML page static class MainPageHandler implements
 * HttpHandler {
 * 
 * @Override public void handle(HttpExchange exchange) throws IOException {
 * String html = getHTML();
 * 
 * exchange.getResponseHeaders().set("Content-Type", "text/html");
 * exchange.sendResponseHeaders(200, html.length());
 * 
 * OutputStream os = exchange.getResponseBody(); os.write(html.getBytes());
 * os.close(); }
 * 
 * private String getHTML() { // Try to load from file first try { return
 * readFileAsString("data/web/autoskilling.html"); } catch (Exception e) { //
 * Return embedded HTML if file not found return getEmbeddedHTML(); } }
 * 
 * // Java 8 compatible file reading private String readFileAsString(String
 * filePath) throws IOException { StringBuilder content = new StringBuilder();
 * Scanner scanner = new Scanner(new File(filePath)); while
 * (scanner.hasNextLine()) { content.append(scanner.nextLine()).append("\n"); }
 * scanner.close(); return content.toString(); }
 * 
 * private String getEmbeddedHTML() { StringBuilder html = new StringBuilder();
 * 
 * html.append("<!DOCTYPE html>"); html.append("<html>"); html.append("<head>");
 * html.append("<title>Auto Skilling Interface</title>"); html.
 * append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
 * ); html.append("<style>"); html.
 * append("body { font-family: Arial, sans-serif; background: #2c3e50; color: white; padding: 20px; text-align: center; }"
 * ); html.
 * append(".container { max-width: 500px; margin: 0 auto; background: #34495e; padding: 30px; border-radius: 10px; }"
 * ); html.append("h1 { color: #ffd700; margin-bottom: 20px; }"); html.
 * append("input { width: 90%; padding: 12px; font-size: 16px; margin: 10px; border: none; border-radius: 5px; text-align: center; }"
 * ); html.
 * append("button { width: 95%; padding: 12px; font-size: 16px; background: #27ae60; color: white; border: none; border-radius: 5px; cursor: pointer; margin: 10px; }"
 * ); html.append("button:hover { background: #2ecc71; }");
 * html.append("button.stop { background: #e74c3c; }");
 * html.append("button.stop:hover { background: #c0392b; }");
 * html.append(".status { margin: 15px; padding: 15px; border-radius: 5px; }");
 * html.append(".success { background: #27ae60; }");
 * html.append(".error { background: #e74c3c; }");
 * html.append(".info { background: #3498db; }");
 * html.append(".hidden { display: none; }"); html.append("</style>");
 * html.append("</head>"); html.append("<body>");
 * 
 * html.append("<div class=\"container\">");
 * html.append("<h1>🎮 Auto Skilling Control</h1>");
 * 
 * // Login Section html.append("<div id=\"loginSection\">");
 * html.append("<h3>Enter Your PIN</h3>"); html.
 * append("<p>Get your PIN in-game by typing: <strong>::webpin</strong></p>");
 * html.
 * append("<input type=\"text\" id=\"pinInput\" placeholder=\"000000\" maxlength=\"6\">"
 * ); html.append("<br>");
 * html.append("<button onclick=\"login()\">🔓 Access Interface</button>");
 * html.append("</div>");
 * 
 * // Main Section (hidden initially)
 * html.append("<div id=\"mainSection\" class=\"hidden\">");
 * html.append("<h3>Welcome, <span id=\"playerName\">Player</span>!</h3>");
 * html.
 * append("<div id=\"statusDisplay\" class=\"status info\">Loading...</div>");
 * 
 * // Character Stats html.append("<div class=\"status info\">");
 * html.append("Combat Level: <span id=\"combatLevel\">Loading...</span> | ");
 * html.append("Time Left: <span id=\"timeRemaining\">Loading...</span><br>");
 * html.
 * append("Current Skill Level: <span id=\"currentSkillLevel\">--</span> | ");
 * html.append("Session XP: <span id=\"sessionXP\">--</span> | ");
 * html.append("XP/Hour: <span id=\"xpPerHour\">--</span>");
 * html.append("</div>");
 * 
 * html.append("<h4>Quick Actions:</h4>"); html.
 * append("<button onclick=\"startSkilling('woodcutting')\">🌲 Start Woodcutting</button>"
 * ); html.
 * append("<button onclick=\"startSkilling('mining')\">⛏️ Start Mining</button>"
 * ); html.
 * append("<button onclick=\"startSkilling('fishing')\">🎣 Start Fishing</button>"
 * ); html.
 * append("<button onclick=\"stopSkilling()\" class=\"stop\">🛑 Stop Auto Skilling</button>"
 * ); html.append("<br><br>");
 * html.append("<button onclick=\"refreshStatus()\">🔄 Refresh Status</button>"
 * ); html.append("<button onclick=\"logout()\">🚪 Logout</button>");
 * html.append("</div>");
 * 
 * html.append("<div id=\"messageArea\"></div>"); html.append("</div>");
 * 
 * // JavaScript html.append("<script>");
 * html.append("var currentPlayer = null;");
 * html.append("var statusInterval = null;");
 * 
 * // Login function html.append("function login() {");
 * html.append("  var pin = document.getElementById('pinInput').value;");
 * html.append("  if (pin.length !== 6) {");
 * html.append("    showMessage('Please enter a 6-digit PIN', 'error');");
 * html.append("    return;"); html.append("  }");
 * html.append("  var xhr = new XMLHttpRequest();");
 * html.append("  xhr.open('POST', '/api/auth', true);");
 * html.append("  xhr.setRequestHeader('Content-Type', 'application/json');");
 * html.append("  xhr.onreadystatechange = function() {");
 * html.append("    if (xhr.readyState === 4 && xhr.status === 200) {");
 * html.append("      var data = JSON.parse(xhr.responseText);");
 * html.append("      if (data.success) {");
 * html.append("        currentPlayer = data.username;"); html.
 * append("        document.getElementById('playerName').textContent = data.username;"
 * ); html.
 * append("        document.getElementById('loginSection').classList.add('hidden');"
 * ); html.
 * append("        document.getElementById('mainSection').classList.remove('hidden');"
 * ); html.
 * append("        if (data.combatLevel) document.getElementById('combatLevel').textContent = data.combatLevel;"
 * ); html.
 * append("        if (data.timeRemaining) document.getElementById('timeRemaining').textContent = data.timeRemaining;"
 * ); html.append("        showMessage('Login successful!', 'success');");
 * html.append("        startStatusUpdates();"); html.append("      } else {");
 * html.append("        showMessage(data.message || 'Invalid PIN', 'error');");
 * html.append("      }"); html.append("    }"); html.append("  };");
 * html.append("  xhr.send(JSON.stringify({ pin: pin }));"); html.append("}");
 * 
 * // Start skilling function html.append("function startSkilling(skill) {");
 * html.append("  if (!currentPlayer) return;");
 * html.append("  var xhr = new XMLHttpRequest();");
 * html.append("  xhr.open('POST', '/api/start', true);");
 * html.append("  xhr.setRequestHeader('Content-Type', 'application/json');");
 * html.append("  xhr.onreadystatechange = function() {");
 * html.append("    if (xhr.readyState === 4 && xhr.status === 200) {");
 * html.append("      var data = JSON.parse(xhr.responseText);"); html.
 * append("      showMessage(data.message, data.success ? 'success' : 'error');"
 * ); html.append("      updateStatus();"); html.append("    }");
 * html.append("  };"); html.
 * append("  xhr.send(JSON.stringify({ username: currentPlayer, skill: skill }));"
 * ); html.append("}");
 * 
 * // Stop skilling function html.append("function stopSkilling() {");
 * html.append("  if (!currentPlayer) return;");
 * html.append("  var xhr = new XMLHttpRequest();");
 * html.append("  xhr.open('POST', '/api/stop', true);");
 * html.append("  xhr.setRequestHeader('Content-Type', 'application/json');");
 * html.append("  xhr.onreadystatechange = function() {");
 * html.append("    if (xhr.readyState === 4 && xhr.status === 200) {");
 * html.append("      var data = JSON.parse(xhr.responseText);"); html.
 * append("      showMessage(data.message, data.success ? 'success' : 'error');"
 * ); html.append("      updateStatus();"); html.append("    }");
 * html.append("  };");
 * html.append("  xhr.send(JSON.stringify({ username: currentPlayer }));");
 * html.append("}");
 * 
 * // Update status function html.append("function updateStatus() {");
 * html.append("  if (!currentPlayer) return;");
 * html.append("  var xhr = new XMLHttpRequest();");
 * html.append("  xhr.open('GET', '/api/status?player=' + currentPlayer, true);"
 * ); html.append("  xhr.onreadystatechange = function() {");
 * html.append("    if (xhr.readyState === 4 && xhr.status === 200) {");
 * html.append("      var data = JSON.parse(xhr.responseText);");
 * html.append("      if (data.success) {"); html.
 * append("        var statusText = data.isActive ? '🟢 Active: ' + data.currentSkill : '🔴 Idle';"
 * ); html.
 * append("        if (data.isActive && data.currentSkillLevel) statusText += ' (Level ' + data.currentSkillLevel + ')';"
 * ); html.
 * append("        if (data.skillProgress) statusText += ' - ' + data.skillProgress + '% to next';"
 * ); html.
 * append("        document.getElementById('statusDisplay').textContent = statusText;"
 * ); html.
 * append("        document.getElementById('statusDisplay').className = 'status ' + (data.isActive ? 'success' : 'info');"
 * ); html.
 * append("        if (data.combatLevel) document.getElementById('combatLevel').textContent = data.combatLevel;"
 * ); html.
 * append("        if (data.timeRemaining) document.getElementById('timeRemaining').textContent = data.timeRemaining;"
 * ); html.
 * append("        if (data.currentSkillLevel) document.getElementById('currentSkillLevel').textContent = data.currentSkillLevel;"
 * ); html.
 * append("        if (data.sessionXPGained) document.getElementById('sessionXP').textContent = '+' + Math.floor(data.sessionXPGained);"
 * ); html.
 * append("        if (data.sessionXPPerHour) document.getElementById('xpPerHour').textContent = Math.floor(data.sessionXPPerHour) + '/hr';"
 * ); html.append("      }"); html.append("    }"); html.append("  };");
 * html.append("  xhr.send();"); html.append("}");
 * 
 * // Helper functions html.
 * append("function refreshStatus() { updateStatus(); showMessage('Status refreshed', 'info'); }"
 * );
 * 
 * html.append("function startStatusUpdates() {");
 * html.append("  updateStatus();");
 * html.append("  statusInterval = setInterval(updateStatus, 3000);");
 * html.append("}");
 * 
 * html.append("function logout() {"); html.append("  currentPlayer = null;");
 * html.append("  if (statusInterval) clearInterval(statusInterval);"); html.
 * append("  document.getElementById('loginSection').classList.remove('hidden');"
 * ); html.
 * append("  document.getElementById('mainSection').classList.add('hidden');");
 * html.append("  document.getElementById('pinInput').value = '';");
 * html.append("  showMessage('Logged out', 'info');"); html.append("}");
 * 
 * html.append("function showMessage(message, type) {");
 * html.append("  var messageArea = document.getElementById('messageArea');");
 * html.
 * append("  messageArea.innerHTML = '<div class=\"status ' + type + '\">' + message + '</div>';"
 * );
 * html.append("  setTimeout(function() { messageArea.innerHTML = ''; }, 4000);"
 * ); html.append("}");
 * 
 * html.append("</script>"); html.append("</body>"); html.append("</html>");
 * 
 * return html.toString(); } }
 * 
 * // Enhanced AuthHandler with skill data static class AuthHandler implements
 * HttpHandler {
 * 
 * @Override public void handle(HttpExchange exchange) throws IOException { //
 * Handle CORS preflight if ("OPTIONS".equals(exchange.getRequestMethod())) {
 * exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
 * exchange.getResponseHeaders().set("Access-Control-Allow-Methods",
 * "POST, GET, OPTIONS");
 * exchange.getResponseHeaders().set("Access-Control-Allow-Headers",
 * "Content-Type"); exchange.sendResponseHeaders(200, 0);
 * exchange.getResponseBody().close(); return; }
 * 
 * if ("POST".equals(exchange.getRequestMethod())) { String body =
 * readInputStream(exchange.getRequestBody());
 * 
 * try { JSONParser parser = new JSONParser(); JSONObject request = (JSONObject)
 * parser.parse(body); String pin = (String) request.get("pin");
 * 
 * Player player = WebAuthManager.validatePIN(pin);
 * System.out.println("DEBUG: Looking for PIN: " + pin); if (player != null) {
 * System.out.println("DEBUG: Found player: " + player.getUsername()); } else {
 * System.out.println("DEBUG: PIN validation failed - no player found"); }
 * 
 * JSONObject response = new JSONObject(); if (player != null) {
 * response.put("success", true); response.put("username",
 * player.getUsername()); response.put("message",
 * "🎉 Authentication successful! Welcome, " + player.getUsername() + "!");
 * 
 * // Enhanced player info for web interface response.put("combatLevel",
 * player.getSkills().getCombatLevel()); response.put("timeRemaining",
 * AutoSkillingManager.getRemainingTimeWeb(player));
 * response.put("currentStatus", AutoSkillingManager.getStatusInfoWeb(player));
 * 
 * // Add skill levels response.put("skillLevels",
 * AutoSkillingManager.getSkillLevelsWeb(player));
 * 
 * // Add current skill info if auto-skilling if
 * (AutoSkillingManager.isAutoSkillingWeb(player)) {
 * response.put("currentSkill", AutoSkillingManager.getCurrentSkillWeb(player));
 * response.put("currentSkillLevel",
 * AutoSkillingManager.getCurrentSkillLevel(player));
 * response.put("skillProgress",
 * AutoSkillingManager.getSkillProgressPercent(player)); }
 * 
 * } else { response.put("success", false); response.put("message",
 * "❌ Invalid or expired PIN. Get a new PIN with ::webpin command in-game."); }
 * 
 * sendJSONResponse(exchange, response); } catch (Exception e) { JSONObject
 * error = new JSONObject(); error.put("success", false); error.put("message",
 * "❌ Invalid request format"); sendJSONResponse(exchange, error); } } } }
 * 
 * // StartHandler with woodcutting parameter support static class StartHandler
 * implements HttpHandler {
 * 
 * @Override public void handle(HttpExchange exchange) throws IOException { //
 * Handle CORS preflight if ("OPTIONS".equals(exchange.getRequestMethod())) {
 * exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
 * exchange.getResponseHeaders().set("Access-Control-Allow-Methods",
 * "POST, GET, OPTIONS");
 * exchange.getResponseHeaders().set("Access-Control-Allow-Headers",
 * "Content-Type"); exchange.sendResponseHeaders(200, 0);
 * exchange.getResponseBody().close(); return; }
 * 
 * if ("POST".equals(exchange.getRequestMethod())) { String body =
 * readInputStream(exchange.getRequestBody());
 * 
 * try { JSONParser parser = new JSONParser(); JSONObject request = (JSONObject)
 * parser.parse(body); String playerName = (String) request.get("username");
 * String skill = (String) request.get("skill");
 * 
 * // Enhanced woodcutting parameters String mode = (String)
 * request.get("mode"); // "auto" or "specific" String treeType = (String)
 * request.get("tree"); // "normal", "oak", "maple", "yew" String
 * inventoryAction = (String) request.get("inventoryAction"); // "bank", "drop",
 * "stop"
 * 
 * Player player = World.getPlayerByDisplayName(playerName);
 * 
 * JSONObject response = new JSONObject(); if (player != null) { // Set
 * woodcutting-specific parameters if it's woodcutting if
 * ("woodcutting".equalsIgnoreCase(skill)) { setupWoodcuttingParameters(player,
 * mode, treeType, inventoryAction); }
 * 
 * boolean success = AutoSkillingManager.startAutoSkillingWeb(player, skill);
 * 
 * if (success) { String message = buildSuccessMessage(skill, mode, treeType,
 * inventoryAction); response.put("success", true); response.put("message",
 * message); response.put("timeRemaining",
 * AutoSkillingManager.getRemainingTimeWeb(player)); response.put("status",
 * AutoSkillingManager.getStatusInfoWeb(player)); } else {
 * response.put("success", false); response.put("message",
 * "❌ Failed to start auto " + skill + ". Check requirements and try again."); }
 * } else { response.put("success", false); response.put("message",
 * "❌ Player not found or offline. Make sure you're logged into the game."); }
 * 
 * sendJSONResponse(exchange, response); } catch (Exception e) { JSONObject
 * error = new JSONObject(); error.put("success", false); error.put("message",
 * "❌ Invalid request format: " + e.getMessage()); sendJSONResponse(exchange,
 * error); } } }
 * 
 *//**
	 * Set up woodcutting-specific parameters based on web interface selection
	 */
/*
 * private void setupWoodcuttingParameters(Player player, String mode, String
 * treeType, String inventoryAction) { // Set woodcutting mode (default to AUTO
 * if not specified) if (mode == null || mode.isEmpty()) { mode = "auto"; //
 * Default mode }
 * 
 * // Normalize mode to uppercase for consistency with your handler String
 * normalizedMode = mode.toUpperCase();
 * player.setAutoWoodcuttingMode(normalizedMode);
 * 
 * // Set specific tree type if provided and mode is SPECIFIC if
 * ("SPECIFIC".equals(normalizedMode) && treeType != null &&
 * !treeType.isEmpty()) { TreeDefinitions selectedTree =
 * parseTreeType(treeType); if (selectedTree != null) {
 * player.setAutoWoodcuttingTree(selectedTree); } } else if
 * ("AUTO".equals(normalizedMode)) { // In AUTO mode, clear any previously set
 * specific tree // The handler will automatically find the best tree
 * player.setAutoWoodcuttingTree(null); }
 * 
 * // Set inventory action (bank, drop, stop) if (inventoryAction != null &&
 * !inventoryAction.isEmpty()) { InventoryAction action =
 * parseInventoryAction(inventoryAction); if (action != null) {
 * player.setSkillingInventoryAction(action); } } }
 * 
 *//**
	 * Convert string inventory action to InventoryAction enum
	 */
/*
 * private InventoryAction parseInventoryAction(String inventoryAction) { if
 * (inventoryAction == null) return null;
 * 
 * try { switch (inventoryAction.toLowerCase().trim()) { case "bank": return
 * InventoryAction.AUTO_BANK; case "drop": return InventoryAction.AUTO_DROP;
 * case "stop": return InventoryAction.STOP_WHEN_FULL; default: return
 * InventoryAction.AUTO_BANK; // Default fallback } } catch (Exception e) {
 * return InventoryAction.AUTO_BANK; // Default fallback } }
 * 
 *//**
	 * Convert string tree type to TreeDefinitions enum
	 */
/*
 * private TreeDefinitions parseTreeType(String treeType) { if (treeType ==
 * null) return null;
 * 
 * try { switch (treeType.toLowerCase().trim()) { case "normal": return
 * TreeDefinitions.NORMAL; case "oak": return TreeDefinitions.OAK; case "maple":
 * return TreeDefinitions.MAPLE; case "yew": return TreeDefinitions.YEW;
 * default: return null; } } catch (Exception e) { return null; } }
 * 
 *//**
	 * Build appropriate success message based on skill and parameters
	 *//*
		 * private String buildSuccessMessage(String skill, String mode, String
		 * treeType, String inventoryAction) { if
		 * ("woodcutting".equalsIgnoreCase(skill)) { StringBuilder message = new
		 * StringBuilder();
		 * 
		 * if ("auto".equalsIgnoreCase(mode)) { message.
		 * append("🤖 Auto woodcutting started! Will automatically use best available trees."
		 * ); } else if ("specific".equalsIgnoreCase(mode) && treeType != null) { String
		 * treeName = treeType.substring(0, 1).toUpperCase() +
		 * treeType.substring(1).toLowerCase();
		 * message.append("🎯 Auto woodcutting started! Cutting ").append(treeName).
		 * append(" trees only."); } else {
		 * message.append("🚀 Auto woodcutting started!"); }
		 * 
		 * // Add inventory action info if (inventoryAction != null) { switch
		 * (inventoryAction.toLowerCase()) { case "bank":
		 * message.append(" 🏦 Will auto-bank when full."); break; case "drop":
		 * message.append(" 🗑️ Will auto-drop when full."); break; case "stop":
		 * message.append(" ⏹️ Will stop when full."); break; } }
		 * 
		 * return message.toString(); } else { String skillName = skill.substring(0,
		 * 1).toUpperCase() + skill.substring(1).toLowerCase(); return "🚀 Auto " +
		 * skillName + " started successfully!"; } } }
		 * 
		 * // StopHandler static class StopHandler implements HttpHandler {
		 * 
		 * @Override public void handle(HttpExchange exchange) throws IOException { //
		 * Handle CORS preflight if ("OPTIONS".equals(exchange.getRequestMethod())) {
		 * exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
		 * exchange.getResponseHeaders().set("Access-Control-Allow-Methods",
		 * "POST, GET, OPTIONS");
		 * exchange.getResponseHeaders().set("Access-Control-Allow-Headers",
		 * "Content-Type"); exchange.sendResponseHeaders(200, 0);
		 * exchange.getResponseBody().close(); return; }
		 * 
		 * if ("POST".equals(exchange.getRequestMethod())) { String body =
		 * readInputStream(exchange.getRequestBody());
		 * 
		 * try { JSONParser parser = new JSONParser(); JSONObject request = (JSONObject)
		 * parser.parse(body); String playerName = (String) request.get("username");
		 * 
		 * Player player = World.getPlayerByDisplayName(playerName);
		 * 
		 * JSONObject response = new JSONObject(); if (player != null) { boolean success
		 * = AutoSkillingManager.stopAutoSkillingWeb(player);
		 * 
		 * if (success) { response.put("success", true); response.put("message",
		 * "🛑 Auto-skilling stopped successfully!"); response.put("timeRemaining",
		 * AutoSkillingManager.getRemainingTimeWeb(player)); } else {
		 * response.put("success", false); response.put("message",
		 * "⚠️ Auto-skilling was not running or already stopped."); } } else {
		 * response.put("success", false); response.put("message",
		 * "❌ Player not found or offline. Make sure you're logged into the game."); }
		 * 
		 * sendJSONResponse(exchange, response); } catch (Exception e) { JSONObject
		 * error = new JSONObject(); error.put("success", false); error.put("message",
		 * "❌ Invalid request format: " + e.getMessage()); sendJSONResponse(exchange,
		 * error); } } } }
		 * 
		 * // Enhanced StatusHandler with comprehensive skill data static class
		 * StatusHandler implements HttpHandler {
		 * 
		 * @Override public void handle(HttpExchange exchange) throws IOException { //
		 * Handle CORS preflight if ("OPTIONS".equals(exchange.getRequestMethod())) {
		 * exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
		 * exchange.getResponseHeaders().set("Access-Control-Allow-Methods",
		 * "POST, GET, OPTIONS");
		 * exchange.getResponseHeaders().set("Access-Control-Allow-Headers",
		 * "Content-Type"); exchange.sendResponseHeaders(200, 0);
		 * exchange.getResponseBody().close(); return; }
		 * 
		 * String query = exchange.getRequestURI().getQuery(); String playerName =
		 * getQueryParam(query, "player");
		 * 
		 * Player player = World.getPlayerByDisplayName(playerName);
		 * 
		 * JSONObject response = new JSONObject(); if (player != null) { try { boolean
		 * isActive = AutoSkillingManager.isAutoSkillingWeb(player); String currentSkill
		 * = AutoSkillingManager.getCurrentSkillWeb(player); String statusInfo =
		 * AutoSkillingManager.getStatusInfoWeb(player); String timeRemaining =
		 * AutoSkillingManager.getRemainingTimeWeb(player);
		 * 
		 * response.put("success", true); response.put("isActive", isActive);
		 * response.put("currentSkill", currentSkill); response.put("username",
		 * player.getUsername()); response.put("statusInfo", statusInfo);
		 * response.put("timeRemaining", timeRemaining);
		 * 
		 * // Enhanced player info response.put("combatLevel",
		 * player.getSkills().getCombatLevel()); response.put("playerLevel",
		 * player.getSkills().getCombatLevel()); // Keep for backward compatibility
		 * response.put("location", getPlayerLocationInfo(player));
		 * 
		 * // Add comprehensive skill data response.put("skillLevels",
		 * AutoSkillingManager.getSkillLevelsWeb(player));
		 * 
		 * // ADDED: Complete XP data for the enhanced website response.put("totalXP",
		 * AutoSkillingManager.getTotalXP(player)); // Total XP across ALL skills
		 * response.put("woodcuttingXP", AutoSkillingManager.getWoodcuttingXP(player));
		 * // Woodcutting XP specifically response.put("currentSkillXP",
		 * AutoSkillingManager.getCurrentSkillXP(player)); // Current active skill XP
		 * 
		 * if (isActive) { // Current skill details response.put("currentSkillLevel",
		 * AutoSkillingManager.getCurrentSkillLevel(player));
		 * response.put("skillProgress",
		 * AutoSkillingManager.getSkillProgressPercent(player));
		 * 
		 * // Session statistics response.put("sessionXPGained",
		 * AutoSkillingManager.getSessionXPGained(player));
		 * response.put("sessionXPPerHour",
		 * AutoSkillingManager.getSessionXPPerHour(player));
		 * response.put("sessionStats", AutoSkillingManager.getSessionStatsWeb(player));
		 * 
		 * // Auto-skilling state details AutoSkillingManager.AutoSkillingState state =
		 * player.getAutoSkillingState(); response.put("skillingState", state.name());
		 * 
		 * // Inventory info response.put("inventorySlots",
		 * player.getInventory().getFreeSlots()); response.put("inventoryFull",
		 * player.getInventory().getFreeSlots() <= 1);
		 * 
		 * // Additional status details based on state switch (state) { case WORKING:
		 * response.put("actionStatus", "actively_skilling");
		 * response.put("actionDetails", "Currently " + currentSkill.toLowerCase());
		 * break; case WALKING_TO_BANK: response.put("actionStatus", "moving_to_bank");
		 * response.put("actionDetails", "Walking to bank (inventory full)"); break;
		 * case BANKING: response.put("actionStatus", "banking");
		 * response.put("actionDetails", "Depositing items at bank"); break; case
		 * WALKING_TO_RESOURCE: response.put("actionStatus", "returning_to_area");
		 * response.put("actionDetails", "Returning to skilling area"); break; } } else
		 * { response.put("actionStatus", "idle"); response.put("actionDetails",
		 * "Character is not auto-skilling"); }
		 * 
		 * } catch (Exception e) { response.put("success", false);
		 * response.put("message", "Error retrieving player status: " + e.getMessage());
		 * e.printStackTrace(); // ADDED: Print stack trace for debugging } } else {
		 * response.put("success", false); response.put("message",
		 * "Player not found or offline. Make sure you're logged into the game."); }
		 * 
		 * sendJSONResponse(exchange, response); } }
		 * 
		 * // Helper method to get player location info private static String
		 * getPlayerLocationInfo(Player player) { try { // Check if player is in
		 * skilling hub if (AutoSkillingManager.isInSkillingHub(player)) { return
		 * "Skilling Hub"; } else { return "Outside Skilling Hub"; } } catch (Exception
		 * e) { return "Unknown"; } }
		 * 
		 * // Java 8 compatible method to read InputStream private static String
		 * readInputStream(InputStream inputStream) throws IOException { StringBuilder
		 * result = new StringBuilder(); BufferedReader reader = new BufferedReader(new
		 * InputStreamReader(inputStream)); String line; while ((line =
		 * reader.readLine()) != null) { result.append(line); } return
		 * result.toString(); }
		 * 
		 * // Helper methods private static void sendJSONResponse(HttpExchange exchange,
		 * JSONObject json) throws IOException { String response = json.toJSONString();
		 * byte[] responseBytes = response.getBytes("UTF-8"); // Get actual bytes
		 * 
		 * // Add CORS headers exchange.getResponseHeaders().set("Content-Type",
		 * "application/json; charset=UTF-8");
		 * exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
		 * exchange.getResponseHeaders().set("Access-Control-Allow-Methods",
		 * "GET, POST, OPTIONS");
		 * exchange.getResponseHeaders().set("Access-Control-Allow-Headers",
		 * "Content-Type");
		 * 
		 * // FIXED: Use actual byte length instead of character length
		 * exchange.sendResponseHeaders(200, responseBytes.length);
		 * 
		 * OutputStream os = exchange.getResponseBody(); os.write(responseBytes); //
		 * Write the same bytes we measured os.close(); }
		 * 
		 * private static String getQueryParam(String query, String param) { if (query
		 * != null) { String[] pairs = query.split("&"); for (String pair : pairs) {
		 * String[] keyValue = pair.split("="); if (keyValue.length == 2 &&
		 * keyValue[0].equals(param)) { return keyValue[1]; } } } return null; } }
		 */