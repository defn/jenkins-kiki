import hudson.util.Secret
import com.cloudbees.plugins.credentials.CredentialsScope
import com.datapipe.jenkins.vault.credentials.VaultAppRoleCredential

def call(pipelineRoleId, secretId) {
    VaultAppRoleCredential pipelineCredential = new VaultAppRoleCredential(
    CredentialsScope.GLOBAL,
    env.BUILD_TAG, env.BUILD_TAG,
    pipelineRoleId,
    Secret.fromString(secretId),
    "approle"
  )

  def pipelineConfiguration = [
    vaultCredential: pipelineCredential
  ]

  return pipelineConfiguration
}
