

    ╔═══════════════════════════════════════════════════════════════════════╗
    ║        Local Generative AI for Android Applications                   ║
    ╚═══════════════════════════════════════════════════════════════════════╝

# Welcome! 🚀

Get ready to dive into the exciting world of local generative AI on Android! This workshop will guide you through implementing powerful AI capabilities directly on Android devices, ensuring privacy and offline functionality.

## Prerequisites 📱

Before starting the workshop, please ensure you have:

- Android Studio (Latest Version)
- A physical Android device OR Android Emulator
- Basic knowledge of Android development
- Enthusiasm to learn!

## BYOD (Bring Your Own Device) 💻

For the best experience, we recommend:
- An Android device running Android 10 or higher
- At least 4GB of RAM
- Minimum 2GB of free storage space

## Setting Up the Environment 🛠️

### Android Emulator Requirements
If using an emulator, configure it with:
- API Level 26 or higher 
- At least 8GB of RAM
- Virtual device storage: 6GB minimum

## LLM Setup Instructions 🤖

1. Download the required model files:
   - [LLAMA 3](https://huggingface.co/bartowski/Meta-Llama-3-8B-Instruct-GGUF)
   - [Stable LLM](https://huggingface.co/TheBloke/stablelm-zephyr-3b-GGUF)
   - [Gemma LLM](https://drive.google.com/file/d/1-6IcEucPf5lurQmxo3notXrxkhG-ODZj/view?usp=sharing)

2. Setup on Device:
   ```bash
   # Extract and place the .bin files in your Android device at:
   /data/local/tmp/llm
