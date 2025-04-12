# Build stage
FROM node:20 as builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .

# ARG for Vite build-time environment variables
ARG VITE_API_BASE_URL
ARG VITE_IMAGE_BASE_URL

# Make them available at build time
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL
ENV VITE_IMAGE_BASE_URL=$VITE_IMAGE_BASE_URL

RUN npm run build

# Run stage
FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80