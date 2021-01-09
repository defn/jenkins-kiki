def call() {
  sh("/env.sh figlet -f /j/chunky.flf build")

  stage('Build') {
    sh "/env.sh goreleaser build --rm-dist"
  }
}
