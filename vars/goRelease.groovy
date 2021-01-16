def call() {
  stage('Release') {
    sh """
      /env.sh figlet -f /j/chunky.flf release
      install -d -m 0700 /tmp/docker
      install -d -m 0700 /tmp/docker/${env.BUILD_TAG}
      env | grep ^DOCKER_PASSWORD= | cut -d= -f2- | docker login --password-stdin --username ${DOCKER_USERNAME}
      /env.sh goreleaser release --rm-dist
    """
  }
}
