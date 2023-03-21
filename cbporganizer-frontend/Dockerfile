# Build stage
FROM node:16.14-alpine AS build
WORKDIR /app

# Copy package.json and yarn.lock to the container
COPY package.json ./

# Install dependencies
RUN npm install

COPY . .

# Build the app
RUN npm run build

# Install the serve package to serve the built app
RUN npm install -g serve

# Set the command to serve the built app
CMD serve -s build

# Expose port 5000 to allow external access to the application
EXPOSE 3000
