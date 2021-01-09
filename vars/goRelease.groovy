def call() {
  sh("/env.sh figlet -f /j/chunky.flf release")

  stage('Release') {
    sh "install -d -m 0700 /tmp/docker"
    sh "install -d -m 0700 /tmp/docker/${env.BUILD_TAG}"
    sh "env | grep ^DOCKER_PASSWORD= | cut -d= -f2- | docker login --password-stdin --username ${DOCKER_USERNAME}"
    sh "/env.sh goreleaser release --rm-dist"
  }
}
