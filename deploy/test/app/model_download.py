from modelscope.hub.snapshot_download import snapshot_download

def main():
    local_cache_dir = "./hf_model_cache"
    model_id = "BAAI/bge-m3"
    snapshot_download(
        model_id=model_id,
        local_dir=local_cache_dir,
    )
    print(f"✅魔搭下载完成，目录：{local_cache_dir}")

if __name__ == "__main__":
    main()

