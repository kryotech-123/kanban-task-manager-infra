### Configuring self hosted runner on github

- Go to settings
- in the left pane click on `Actions`
    - select runners
    - select New self-hosted runner in the right action pane
    - follow the instructions below on your instance provisioned

## IMPORTANT:
    modify your pipeline to run on self-hosted in the .github/workflow/<pipeline_name.yml>





### Setting up runner instance
Setup a t3.medium for handling pipeline executions
creating a systemd service to ensure runner is running at all times

```
sudo nano /etc/systemd/system/github-runner.service
```

```
[Unit]
Description=GitHub Actions Self-Hosted Runner
After=network.target

[Service]
ExecStart=/home/ubuntu/actions-runner/run.sh
WorkingDirectory=/home/ubuntu/actions-runner
User=ubuntu
Restart=always
RestartSec=5s
Environment="RUNNER_ALLOW_RUNASROOT=1"

[Install]
WantedBy=multi-user.target


```

```
sudo systemctl daemon-reexec
sudo systemctl daemon-reload
sudo systemctl enable github-runner
sudo systemctl start github-runner

```