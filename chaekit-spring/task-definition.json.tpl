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
      "environment": [
        {
          "name": "JWT_EXPIRATION_MS",
          "value": "3600000"
        },
        {
          "name": "ADMIN_USERNAME",
          "value": "admin"
        },
        {
          "name": "SPRING_PROFILES_ACTIVE",
          "value": "prod"
        },
        {
          "name": "EBOOK_BUCKET_NAME",
          "value": "chaekit"
        },
        {
          "name": "AWS_S3_REGION",
          "value": "ap-northeast-2"
        },
        {
          "name": "EBOOK_MAX_FILE_SIZE",
          "value": "20971520"
        },
        {
          "name": "PRESIGNED_URL_EXPIRATION_TIME",
          "value": "3600"
        },
        {
          "name": "CORS_ALLOWED_ORIGINS",
          "value": "http://localhost:5173/"
        },
        {
            "name": "DB_USERNAME",
            "value": ""
        },
        {
            "name": "DB_PASSWORD",
            "value": ""
        },
        {
            "name": "JWT_SECRET",
            "value": ""
        },
        {
            "name": "ADMIN_PASSWORD",
            "value": ""
        },
        {
            "name": "AWS_ACCESS_KEY_ID",
            "value": ""
        },
        {
            "name": "SECRET_ACCESS_KEY",
            "value": ""
        }
      ],
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
