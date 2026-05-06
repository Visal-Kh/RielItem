══════════════════════════════════════════
   RielItem Plugin — LoyaltyMC
   Command: /loyalty
   Version: 1.1
══════════════════════════════════════════

■ REQUIREMENTS
  - Java 21+
  - Paper / Spigot 1.21+
  - Vault plugin
  - EssentialsX plugin

■ BUILD STEPS (ជំហានក្នុងការ Build)

  1. Install Java 21
     https://adoptium.net/

  2. Install Maven
     https://maven.apache.org/download.cgi
     (Download "Binary zip archive" → extract → add to PATH)

  3. Build JAR:
     - Windows: double-click BUILD_WINDOWS.bat
     - Linux/Mac: run ./BUILD_LINUX.sh

  4. JAR file នឹងចេញក្នុង:
     target/RielItem_v1_1_loyalty.jar

  5. Copy JAR ទៅ:
     your-server/plugins/RielItem_v1_1_loyalty.jar

  6. Restart server

■ COMMANDS
  /loyalty give <player> <amount> [qty]
  /loyalty give-all <amount>
  /loyalty withdraw <amount>
  /loyalty balance
  /loyalty list
  /loyalty reload

■ DENOMINATIONS (លុយរៀល)
  100, 500, 1000, 5000, 10000, 50000, 100000

══════════════════════════════════════════
