def call(String m, String font = "chunky") {
  ansiColor('xterm-256color') {
    sh("set +x; /env.sh figlet -w 9999 -f /j/${font}.flf ${m} | lolcat -f; echo")
  }
}
