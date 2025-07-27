import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    vus: 50, // utenti virtuali
    duration: '30s', // durata del test
};

export default function () {
    let res = http.post('http://localhost:8000/configurations');
    check(res, { 'status 200': (r) => r.status === 200 });
    sleep(1);
}