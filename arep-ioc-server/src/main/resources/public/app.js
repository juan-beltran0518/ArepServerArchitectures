document.addEventListener('DOMContentLoaded', function() {
    const helloForm = document.getElementById('helloForm');
    const greetingForm = document.getElementById('postForm');
    const helloResponseDiv = document.getElementById('response');
    const greetingResponseDiv = document.getElementById('postrespmsg');

    helloForm.addEventListener('submit', async function(e) {
        e.preventDefault();
        const name = document.getElementById('name').value;
        helloResponseDiv.textContent = 'Cargando...';
        try {
            const res = await fetch(`/app/hello?name=${encodeURIComponent(name)}`);
            if (!res.ok) throw new Error('Error en la respuesta del servidor');
            const data = await res.json();
            helloResponseDiv.textContent = data.mensaje;
        } catch (err) {
            helloResponseDiv.textContent = 'Error: ' + err.message;
        }
    });

    greetingForm.addEventListener('submit', function(e) {
        e.preventDefault();
        const name = document.getElementById('postname').value;
        greetingResponseDiv.textContent = 'Cargando...';
        fetch(`/greeting?name=${encodeURIComponent(name)}`, { method: 'GET' })
            .then(response => {
                if (!response.ok) throw new Error('Error en la respuesta del servidor');
                return response.text();
            })
            .then(data => {
                greetingResponseDiv.textContent = data;
            })
            .catch(err => {
                greetingResponseDiv.textContent = 'Error: ' + err.message;
            });
    });
});