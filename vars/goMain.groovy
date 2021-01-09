def call(pipelineRoleId, vaultSecrets, nmJob, nmBinary, nmDocker, vendorPrefix) {
  goPrep()

  withCredentials([[
    $class: 'VaultTokenCredentialBinding',
    credentialsId: 'VaultToken',
    vaultAddr: env.VAULT_ADDR ]]) {

    stage ('Secrets') {
      def PIPELINE_SECRET_ID= ''
      env.PIPELINE_SECRET_ID = sh(returnStdout: true, script: "./ci/build ${nmJob}").trim()

      def pipelineConfiguration = creds(pipelineRoleId, env.PIPELINE_SECRET_ID)

      withVault([vaultSecrets: pipelineSecrets, configuration: pipelineConfiguration]) {
        sh("env | grep MEH")
      }
    }

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

  goClean()
}
