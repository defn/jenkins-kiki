def call() {
  stage('Build') {
    sh("/env.sh figlet -f /j/chunky.flf build")
    sh "/env.sh goreleaser build --rm-dist"
  }
}
