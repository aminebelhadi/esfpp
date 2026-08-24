#application build  stage
FROM node:22.20.0-alpine AS builder
WORKDIR /app
COPY package-lock.json .
COPY package.json .
COPY . .
RUN npm install
ARG VITE_BACKEND_URL
ENV VITE_BACKEND_URL=${VITE_BACKEND_URL}
RUN npm run build

#image build stage
FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
EXPOSE 80
CMD [ "nginx", "-g", "daemon off;" ]