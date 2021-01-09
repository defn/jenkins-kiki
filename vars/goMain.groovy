def call(pipelineRoleId, jenkinsSecrets, pipelineSecrets, nmJob, nmBinary, nmDocker, vendorPrefix) {
  goPrep()

  withCredentials([[
    $class: 'VaultTokenCredentialBinding',
    credentialsId: 'VaultToken',
    vaultAddr: env.VAULT_ADDR ]]) {

    stage ('Secrets') {
      def PIPELINE_SECRET_ID= ''
      env.PIPELINE_SECRET_ID = sh(returnStdout: true, script: "./ci/build ${nmJob}").trim()

      def pipelineConfiguration = creds(pipelineRoleId, env.PIPELINE_SECRET_ID)

      withVault([jenkinsSecrets: pipelineSecrets, configuration: pipelineConfiguration]) {
        sh("env | grep MEH")
      }
    }

    withVault([jenkinsSecrets: jenkinsSecrets]) {
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
