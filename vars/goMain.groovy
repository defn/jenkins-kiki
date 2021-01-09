def call(Map param, Closure body) {
  goPrep()

  withCredentials([[
    $class: 'VaultTokenCredentialBinding',
    credentialsId: 'VaultToken',
    vaultAddr: env.VAULT_ADDR ]]) {

    def WRAPPED_SID = ''
    env.WRAPPED_SID = sh(returnStdout: true, script: "/env.sh vault write -field=wrapping_token -wrap-ttl=60s -f auth/approle/role/${param.role}/secret-id").trim()
  
    def UNWRAPPED_SID = ''
    env.UNWRAPPED_SID= sh(returnStdout: true, script: 'set +x; /env.sh vault unwrap -field=secret_id ${WRAPPED_SID}; set -x').trim()

    def pipelineConfiguration = creds(param.roleId, env.UNWRAPPED_SID)

    withVault([vaultSecrets: param.jenkinsSecrets]) {
      withVault([vaultSecrets: param.pipelineSecrets, configuration: pipelineConfiguration]) {
        withEnv(["DOCKER_CONFIG=/tmp/docker/${env.BUILD_TAG}"]) {
          if (env.TAG_NAME) {
            goRelease()
            body()
          }
          else {
            goBuild()
            body()
          }
        }
      }
    }
  }

  goClean()
}
