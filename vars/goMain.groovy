def call(vaultSecrets, nmBinary, nmDocker, vendorPrefix) {
  withVault([vaultSecrets: vaultSecrets]) {
    withEnv(["DOCKER_CONFIG=/tmp/docker/${env.BUILD_TAG}"]) {
      if (env.TAG_NAME) {
        goRelease()

        stage('Test Docker image') {
          sh "/env.sh docker run --rm --entrypoint /" + nmBinary + "  " + nmDocker + ":" + vendorPrefix + "${env.GORELEASER_CURRENT_TAG.minus('v')}-amd64"
        }
      }
      else {
        goBuild()
      }
    }
  }
}
