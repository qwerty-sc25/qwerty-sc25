{
  "family": "ecs-task",
  "containerDefinitions": [
    {
      "name": "ecs-container",
      "image": "880996438467.dkr.ecr.ap-northeast-2.amazonaws.com/chaekit/chaekit_spirngbackend",
      "cpu": 0,
      "portMappings": [
        {
          "name": "ecs-container-80-tcp",
          "containerPort": 80,
          "hostPort": 0,
          "protocol": "tcp",
          "appProtocol": "http"
        }
      ],
      "essential": true,
      "environment": [],
      "environmentFiles": [],
      "mountPoints": [],
      "volumesFrom": [],
      "ulimits": [],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/ecs-task",
          "mode": "non-blocking",
          "awslogs-create-group": "true",
          "max-buffer-size": "25m",
          "awslogs-region": "ap-northeast-2",
          "awslogs-stream-prefix": "ecs"
        },
        "secretOptions": []
      },
      "systemControls": []
    }
  ],
  "executionRoleArn": "arn:aws:iam::880996438467:role/ecsTaskExecutionRole",
  "networkMode": "bridge",
  "volumes": [],
  "placementConstraints": [],
  "requiresCompatibilities": [
    "EC2"
  ],
  "cpu": "256",
  "memory": "256",
  "runtimePlatform": {
    "cpuArchitecture": "X86_64",
    "operatingSystemFamily": "LINUX"
  }
}
