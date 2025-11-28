# Build stage
FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
# Use npm ci if lockfile exists, otherwise fallback to npm install
RUN if [ -f package-lock.json ]; then npm ci --legacy-peer-deps; else npm install --legacy-peer-deps; fi
COPY . .
# Build the React app
RUN npm run build

# Runtime stage using Nginx
FROM nginx:stable-alpine
COPY --from=builder /app/build /usr/share/nginx/html
# Custom nginx config to support SPA routing
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx","-g","daemon off;"]
