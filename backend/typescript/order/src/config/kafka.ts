import { config } from "./order";

const { kafka } = config;

export const kafkaBroker = `${kafka.host}:9092`;
console.log('kafka Host is %s', kafkaBroker);
