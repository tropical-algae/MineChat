<div align="center"><img src="asset/logo.png"/></div>
<h3 align="center"> A mod that integrates GPT into Minecraft.</h3>

------------------------------

## Mine Chat

Now you could chat with villager in minecraft with a simple config, just face to them and texting.
#### 🏷️ Version Compatibility

- **Minecraft**: `1.20.4`
- **Forge**: `49.1.0` ~ `49.2.0`
- Supports both **client** and **server** sides.

#### ⚙️ Basic Configuration

Edit the configuration file located at `config/minechat-common.toml`. The following parameters are required:

|Name|Description|
|---|---|
|`gpt_api`|Model URL compatible with the OpenAI API format|
|`gpt_key`|API key for accessing the model|
|`gpt_model`|Name of the model to be used|

Additionally, you can define custom prompts for villagers of different professions, or specify a default prompt to be shared among them. Please refer to the comments in the configuration file for available parameters.

> 💡 **Tip**: In multiplayer mode, configuring the server alone is sufficient.
