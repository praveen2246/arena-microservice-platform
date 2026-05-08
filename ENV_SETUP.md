# Environment Configuration Guide

This project uses environment variables to manage sensitive configurations like database passwords and secret keys. All secrets are stored in a `.env` file which is gitignored.

## Setup Instructions

### 1. Create `.env` File
Copy the `.env.example` file to create your local `.env` file:

```bash
cp .env.example .env
```

### 2. Configure Environment Variables
Edit the `.env` file and update the values with your actual configuration:

```env
# Database Configuration
POSTGRES_USER=admin
POSTGRES_PASSWORD=your_secure_password_here
POSTGRES_DB=postgres

# JWT Configuration
JWT_SECRET=your_jwt_secret_key_here
JWT_EXPIRATION=86400000

# RabbitMQ Configuration
RABBITMQ_USER=guest
RABBITMQ_PASSWORD=your_rabbitmq_password_here
```

### 3. Load Environment Variables

#### For Docker Compose:
Docker Compose automatically loads the `.env` file from the project root. Simply run:

```bash
docker-compose up
```

#### For Local Development:
Load environment variables in your terminal before running services:

**Linux/macOS:**
```bash
export $(cat .env | xargs)
```

**PowerShell (Windows):**
```powershell
Get-Content .env | ForEach-Object { if ($_ -and !$_.StartsWith('#')) { [Environment]::SetEnvironmentVariable($_.Split('=')[0], $_.Split('=', 2)[1]) } }
```

Or use an IDE plugin to automatically load `.env` files.

## Environment Variables Reference

| Variable | Description | Example |
|----------|-------------|---------|
| `POSTGRES_USER` | PostgreSQL username | `admin` |
| `POSTGRES_PASSWORD` | PostgreSQL password | `your_secure_password` |
| `DATABASE_URL` | JDBC connection URL | `jdbc:postgresql://postgres:5432/user_db` |
| `JWT_SECRET` | JWT signing secret | `404E635266556A586E3272357538782F...` |
| `JWT_EXPIRATION` | JWT token expiration time in ms | `86400000` |
| `RABBITMQ_USER` | RabbitMQ username | `guest` |
| `RABBITMQ_PASSWORD` | RabbitMQ password | `your_secure_password` |
| `RABBITMQ_HOST` | RabbitMQ server hostname | `rabbitmq` |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `docker` |

## Security Best Practices

1. **Never commit `.env` file** - It's already in `.gitignore`
2. **Use strong passwords** - Generate secure passwords for production
3. **Rotate secrets regularly** - Update JWT_SECRET and passwords periodically
4. **Environment-specific configs** - Use different `.env` files for dev/staging/production:
   - `.env.local` for local development
   - `.env.staging` for staging environment
   - `.env.production` for production

## Files Modified

The following configuration files now use environment variables:

- **docker-compose.yml** - PostgreSQL and RabbitMQ credentials
- **config-repo/user-service.yml** - Database and JWT configuration
- **config-repo/post-service.yml** - Database credentials
- **config-repo/notification-service.yml** - Database and RabbitMQ credentials
- **config-repo/\*-docker.yml** - Docker-specific configurations

All files support both environment variables and default values using the format: `${VAR_NAME:default_value}`
