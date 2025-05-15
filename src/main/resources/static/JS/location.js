function openMap(targetMapId, modalId, addressOutputId) {
    navigator.geolocation.getCurrentPosition(function(pos) {
        const lat = pos.coords.latitude;
        const lng = pos.coords.longitude;

        const map = new google.maps.Map(document.getElementById(targetMapId), {
            center: { lat, lng },
            zoom: 15,
        });

        new google.maps.Marker({
            position: { lat, lng },
            map: map,
            title: "내 위치"
        });

        const geocoder = new google.maps.Geocoder();
        geocoder.geocode({ location: { lat, lng } }, (results, status) => {
            if (status === "OK" && results[0]) {
                const areaName = results[0].formatted_address;
                document.getElementById(addressOutputId).textContent = areaName;

                if (document.getElementById("area-name-label")) {
                    document.getElementById("area-name-label").textContent = `(${areaName})`;
                }

                if (document.getElementById("areaName")) document.getElementById("areaName").value = areaName;
                if (document.getElementById("latitude")) document.getElementById("latitude").value = lat;
                if (document.getElementById("longitude")) document.getElementById("longitude").value = lng;
            }
        });

        new bootstrap.Modal(document.getElementById(modalId)).show();
    });
}
