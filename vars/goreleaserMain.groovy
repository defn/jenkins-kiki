def call(Map param, Closure body) {
  node() {
    def success = false

    try {
      goPrep()

      withCredentials([[
        $class: 'VaultTokenCredentialBinding',
        credentialsId: 'VaultToken',
        vaultAddr: env.VAULT_ADDR ]]) {

        sh("/env.sh figlet -f /j/chunky.flf secrets")

        def WRAPPED_SID = ''
        env.WRAPPED_SID = sh(returnStdout: true, script: "/env.sh vault write -field=wrapping_token -wrap-ttl=60s -f auth/approle/role/${param.role}/secret-id").trim()
      
        def UNWRAPPED_SID = ''
        env.UNWRAPPED_SID= sh(returnStdout: true, script: 'set +x; /env.sh vault unwrap -field=secret_id ${WRAPPED_SID}; set -x').trim()

        def pipelineConfiguration = creds(param.roleId, env.UNWRAPPED_SID)

        def jenkinsSecrets = [
          path: 'kv/jenkins/common',
          secretValues: [
            [vaultKey: 'GITHUB_TOKEN'],
            [vaultKey: 'DOCKER_USERNAME'],
            [vaultKey: 'DOCKER_PASSWORD']
          ]
        ]

        withVault([vaultSecrets: param.pipelineSecrets + jenkinsSecrets, configuration: pipelineConfiguration]) {
          withEnv(["DOCKER_CONFIG=/tmp/docker/${env.BUILD_TAG}"]) {
            body()
          }
        }
      }

      goClean()

      success = true
    }
    finally {
      if (success) {
        sh("/env.sh figlet -f /j/broadway.flf ok; echo")
      }
      else {
        sh("/env.sh figlet -f /j/broadway.flf fail; echo")
      }
    }
  }
}
